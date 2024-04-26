package com.dn0ne.moneymate.app.data.repository

import com.dn0ne.moneymate.app.domain.entities.change.Change
import com.dn0ne.moneymate.app.domain.entities.spending.Category
import com.dn0ne.moneymate.app.domain.entities.spending.Spending
import com.dn0ne.moneymate.app.domain.repository.ChangeRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class RealmChangeRepository(private val realm: Realm) : ChangeRepository {
    override fun getChanges(): List<Change> {
        return (
                getInsertSpendingChanges() +
                        getUpdateSpendingChanges() +
                        getDeleteSpendingChanges() +
                        getInsertCategoryChanges() +
                        getUpdateCategoryChanges() +
                        getDeleteCategoryChanges()
                ).sortedBy { it.changeId.timestamp }
    }

    override suspend fun clearChanges() {
        realm.write {
            val queriedChanges = getChanges()
            try {
                queriedChanges.forEach { change ->
                    when(change) {
                        is Change.DeleteCategory -> findLatest(change)?.let { delete(it) }
                        is Change.DeleteSpending -> findLatest(change)?.let { delete(it) }
                        is Change.InsertCategory -> findLatest(change)?.let { delete(it) }
                        is Change.InsertSpending -> findLatest(change)?.let { delete(it) }
                        is Change.UpdateCategory -> findLatest(change)?.let { delete(it) }
                        is Change.UpdateSpending -> findLatest(change)?.let { delete(it) }
                    }
                }
            } catch (e: IllegalArgumentException) {
                println("REALM ERROR: ${e.message}")
            }
        }
    }

    override suspend fun insertChange(change: Change) {
        realm.write {
            copyToRealm(
                instance = when (change) {
                    is Change.InsertSpending -> {
                        change.document = findLatest(
                            realm.query<Spending>(
                                query = "id == $0",
                                change.document!!.id
                            ).first().find()!!
                        )
                        change
                    }

                    is Change.UpdateSpending -> {
                        change.document = findLatest(
                            realm.query<Spending>(
                                query = "id == $0",
                                change.document!!.id
                            ).first().find()!!
                        )
                        change
                    }

                    is Change.DeleteSpending -> {
                        change
                    }

                    is Change.InsertCategory -> {
                        change.document = findLatest(
                            realm.query<Category>(
                                query = "id == $0",
                                change.document!!.id
                            ).first().find()!!
                        )
                        change
                    }

                    is Change.UpdateCategory -> {
                        change.document = findLatest(
                            realm.query<Category>(
                                query = "id == $0",
                                change.document!!.id
                            ).first().find()!!
                        )
                        change
                    }

                    is Change.DeleteCategory -> {
                        change
                    }
                }
            )
        }
    }

    private fun getInsertSpendingChanges(): List<Change.InsertSpending> {
        return realm.query<Change.InsertSpending>().find()
    }

    private fun getUpdateSpendingChanges(): List<Change.UpdateSpending> {
        return realm.query<Change.UpdateSpending>().find()
    }

    private fun getDeleteSpendingChanges(): List<Change.DeleteSpending> {
        return realm.query<Change.DeleteSpending>().find()
    }

    private fun getInsertCategoryChanges(): List<Change.InsertCategory> {
        return realm.query<Change.InsertCategory>().find()
    }

    private fun getUpdateCategoryChanges(): List<Change.UpdateCategory> {
        return realm.query<Change.UpdateCategory>().find()
    }

    private fun getDeleteCategoryChanges(): List<Change.DeleteCategory> {
        return realm.query<Change.DeleteCategory>().find()
    }
}