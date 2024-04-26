package com.dn0ne.moneymate.app.domain.sync

import com.russhwolf.settings.Settings

class SyncStorage {
    private val settings = Settings()

    var token: String?
        get() = settings.getStringOrNull("token")
        set(value) {
            value?.let {
                settings.putString("token", it)
            } ?: settings.remove("token")
        }

    var lastSyncedChangeId: String?
        get() = settings.getStringOrNull("lastSyncedChangeId")
        set(value) {
            value?.let {
                settings.putString("lastSyncedChangeId", value)
            } ?: settings.remove("lastSyncedChangeId")
        }

    var email: String?
        get() = settings.getStringOrNull("email")
        set(value) {
            value?.let {
                settings.putString("email", value)
            } ?: settings.remove("email")
        }

    var password: String?
        get() = settings.getStringOrNull("password")
        set(value) {
            value?.let {
                settings.putString("password", value)
            } ?: settings.remove("password")
        }

    var isLoggedIn: Boolean
        get() = settings.getBoolean("isLoggedIn", false)
        set(value) {
            settings.putBoolean("isLoggedIn", value)
        }
}