package com.dn0ne.moneymate.app.domain.repository

import com.dn0ne.moneymate.app.domain.entities.change.Change

interface ChangeRepository {
    fun getChanges(): List<Change>
    suspend fun clearChanges()
    suspend fun insertChange(change: Change)
}