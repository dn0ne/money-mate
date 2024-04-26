package com.dn0ne.moneymate.app.domain.sync

sealed class SyncStatus {
    data class Success(val data: String = ""): SyncStatus()
    data object InProgress: SyncStatus()
    data object Timeout: SyncStatus()
    data object NoNetwork: SyncStatus()
    data object IncorrectUsernameOrPassword: SyncStatus()
    data object UserAlreadyExists: SyncStatus()
    data object InvalidUserData: SyncStatus()
    data object UnknownErrorWhileSigningUp: SyncStatus()
}