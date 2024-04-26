@file:UseSerializers(
    RealmListKSerializer::class,
    RealmSetKSerializer::class,
    RealmAnyKSerializer::class,
    RealmInstantKSerializer::class,
    MutableRealmIntKSerializer::class,
    RealmUUIDKSerializer::class
)

package com.dn0ne.moneymate.app.data.remote

import com.dn0ne.moneymate.app.domain.entities.change.Change
import com.dn0ne.moneymate.app.domain.entities.user.User
import com.dn0ne.moneymate.app.domain.sync.SyncStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.realm.kotlin.serializers.MutableRealmIntKSerializer
import io.realm.kotlin.serializers.RealmAnyKSerializer
import io.realm.kotlin.serializers.RealmInstantKSerializer
import io.realm.kotlin.serializers.RealmListKSerializer
import io.realm.kotlin.serializers.RealmSetKSerializer
import io.realm.kotlin.serializers.RealmUUIDKSerializer
import kotlinx.serialization.UseSerializers

class SyncApi(
    private val client: HttpClient
) {
    suspend fun signup(user: User): SyncStatus? {
        val response = try {
            client.request("auth/signup") {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                setBody(user)
            }
        } catch (e: HttpRequestTimeoutException) {
            return SyncStatus.Timeout
        } catch (e: Throwable) {
            return SyncStatus.NoNetwork
        }

        return when (response.status) {
            HttpStatusCode.Conflict -> SyncStatus.UserAlreadyExists
            HttpStatusCode.UnprocessableEntity -> SyncStatus.InvalidUserData
            HttpStatusCode.Created -> null
            else -> SyncStatus.UnknownErrorWhileSigningUp
        }
    }

    suspend fun login(user: User): SyncStatus {
        val response = try {
            client.request("auth/login") {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                setBody(user)
            }
        } catch (e: HttpRequestTimeoutException) {
            return SyncStatus.Timeout
        } catch (e: Throwable) {
            return SyncStatus.NoNetwork
        }

        if (response.status == HttpStatusCode.Unauthorized) {
            return SyncStatus.IncorrectUsernameOrPassword
        }

        return SyncStatus.Success(response.body<String>()) // Token
    }

    suspend fun getChanges(token: String): List<Change> {
        val response = client.request("changelog/all") {
            method = HttpMethod.Get
            headers {
                append("Authorization", "Bearer $token")
            }
        }

        when (response.status) {
            HttpStatusCode.NotFound -> {
                return listOf()
            }
        }

        return response.body<List<Change>>()
    }

    suspend fun getChangesAfterId(token: String, changeId: String): List<Change> {
        val response = client.request("changelog/afterId") {
            method = HttpMethod.Post
            headers {
                append("Authorization", "Bearer $token")
            }
            contentType(ContentType.Text.Plain)
            setBody(changeId)
        }

        when (response.status) {
            HttpStatusCode.NotFound -> {
                return listOf()
            }
        }

        return response.body<List<Change>>()
    }

    suspend fun insertChanges(token: String, changes: List<Change>): SyncStatus? {
        return try {
            client.request("changelog/insert") {
                method = HttpMethod.Patch
                headers {
                    append("Authorization", "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(changes)
            }

            null
        } catch (e: HttpRequestTimeoutException) {
            SyncStatus.Timeout
        } catch (e: Throwable) {
            SyncStatus.NoNetwork
        }
    }
}