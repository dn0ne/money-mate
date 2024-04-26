package com.dn0ne.moneymate.app.domain.sync

import com.dn0ne.moneymate.app.data.remote.SyncApi
import com.dn0ne.moneymate.app.domain.entities.change.Change
import com.dn0ne.moneymate.app.domain.entities.user.User
import com.dn0ne.moneymate.app.domain.extensions.filterDeleted
import com.dn0ne.moneymate.app.domain.extensions.optimizeChanges
import com.dn0ne.moneymate.app.domain.repository.ChangeRepository
import com.dn0ne.moneymate.app.domain.repository.SpendingRepository
import kotlinx.coroutines.TimeoutCancellationException

class SyncService(
    private val spendingRepository: SpendingRepository,
    private val changeRepository: ChangeRepository,
    private val syncApi: SyncApi,
    private val syncStorage: SyncStorage = SyncStorage(),
) {

    var isLoggedIn: Boolean
        get() = syncStorage.isLoggedIn
        private set(value) {
            syncStorage.isLoggedIn = value
        }


    private fun rememberUser(user: User) {
        syncStorage.email = user.email
        syncStorage.password = user.password
    }

    fun forgetUser() {
        syncStorage.email = null
        syncStorage.password = null
        isLoggedIn = false
    }

    suspend fun signup(user: User): SyncStatus? {
        rememberUser(user)
        var response = syncApi.signup(user)
        response?.let {
            return it
        }

        response = login(user)
        return response
    }

    suspend fun login(user: User? = null): SyncStatus? {
        var userToLogin: User? = null

        user?.let {
            rememberUser(user)
            userToLogin = user
        } ?: run {
            val email = syncStorage.email
            val password = syncStorage.password

            if (email != null && password != null) {
                userToLogin = User(email = email, password = password)
            }
        }

        userToLogin?.let {
            val response = syncApi.login(it)
            if (response is SyncStatus.Success) {
                syncStorage.token = response.data
                isLoggedIn = true
            } else {
                return response
            }
        } ?: NullPointerException("No user data to log in.")

        return null
    }

    suspend fun syncChanges(): SyncStatus? {
        login()

        val token = syncStorage.token ?: throw SyncException("Sync failed: user is not logged in")

        val remoteChanges = try {
            syncStorage.lastSyncedChangeId?.let { lastChangeId ->
                syncApi.getChangesAfterId(token, lastChangeId)
            } ?: syncApi.getChanges(token)
        } catch (e: TimeoutCancellationException) {
            return SyncStatus.Timeout
        } catch (e: Throwable) {
            e.printStackTrace()
            return SyncStatus.NoNetwork
        }

        if (remoteChanges.isNotEmpty()) {
            applyChanges(remoteChanges)
            remoteChanges.lastOrNull()?.changeId?.toHexString()?.let {
                syncStorage.lastSyncedChangeId = it
            }
        }


        val localChanges = changeRepository.getChanges()
            .optimizeChanges()
            .filterDeleted(remoteChanges)

        if (localChanges.isNotEmpty()) {
            val response = syncApi.insertChanges(token, localChanges)
            response?.let {
                return it
            }

            localChanges.lastOrNull()?.changeId?.toHexString()?.let {
                syncStorage.lastSyncedChangeId = it
            }

            changeRepository.clearChanges()
        }

        return null
    }

    private suspend fun applyChanges(changes: List<Change>) {
        changes.forEach { change ->
            when (change) {
                is Change.InsertSpending -> {
                    change.document?.let { spendingRepository.insertSpending(it) }
                }

                is Change.UpdateSpending -> {
                    change.document?.let { spendingRepository.updateSpending(it) }
                }

                is Change.DeleteSpending -> {
                    change.documentId.let { spendingRepository.deleteSpending(it) }
                }

                is Change.InsertCategory -> {
                    change.document?.let { spendingRepository.insertCategory(it) }
                }

                is Change.UpdateCategory -> {
                    change.document?.let { spendingRepository.updateCategory(it) }
                }

                is Change.DeleteCategory -> {
                    change.documentId.let { spendingRepository.deleteCategory(it) }
                }
            }

            syncStorage.lastSyncedChangeId = change.changeId.toHexString()
        }
    }
}
