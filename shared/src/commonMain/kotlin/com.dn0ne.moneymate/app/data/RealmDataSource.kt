package com.dn0ne.moneymate.app.data

import com.dn0ne.moneymate.app.domain.Category
import com.dn0ne.moneymate.app.domain.DataSource
import com.dn0ne.moneymate.app.domain.Spending
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

/**
 * Realm DataSource implementation
 */
class RealmDataSource(private val realm: Realm) : DataSource {
    override fun getSpendings(): Flow<List<Spending>> {
        return realm.query<Spending>().asFlow().map { it.list.reversed() }
    }

    override suspend fun insertSpending(spending: Spending) {
        realm.write {
            spending.category = findLatest(spending.category!!)
            copyToRealm(instance = spending)
        }
    }

    override suspend fun updateSpending(spending: Spending) {
        realm.write {
            val queriedSpending = findLatest(
                realm.query<Spending>(query = "id == $0", spending.id).first().find()!!
            )

            queriedSpending?.apply {
                category = findLatest(spending.category!!)
                amount = spending.amount
                shortDescription = spending.shortDescription
                shoppingList = spending.shoppingList
            }
        }
    }

    override suspend fun deleteSpending(id: ObjectId) {
        realm.write {
            val queriedSpending = query<Spending>(query = "id == $0", id).first().find()
            try {
                queriedSpending?.let { delete(it) }
            } catch (e: IllegalArgumentException) {
                println("REALM ERROR: spending to be deleted not found")
            }
        }
    }

    override fun getCategories(): Flow<List<Category>> {
        return realm.query<Category>().asFlow().map { it.list }
    }

    override suspend fun insertCategory(category: Category) {
        realm.write {
            copyToRealm(category)
        }
    }

    override suspend fun updateCategory(category: Category) {
        realm.write {
            val queriedCategory = findLatest(
                realm.query<Category>(query = "id == $0", category.id).first().find()!!
            )

            queriedCategory?.apply {
                name = category.name
                iconName = category.iconName
            }
        }
    }

    override suspend fun deleteCategory(id: ObjectId) {
        val queriedCategory = realm.query<Category>(query = "id == $0", id).first().find()

        val queriedSpendings =
            realm.query<Spending>(query = "category == $0", queriedCategory).find()
        coroutineScope {
            launch {
                queriedSpendings.forEach { spending ->
                    deleteSpending(spending.id)
                }
            }
        }

        realm.write {
            try {
                queriedCategory?.let { findLatest(it)?.let { latest -> delete(latest) } }
            } catch (e: IllegalArgumentException) {
                println("REALM ERROR: category to be deleted not found")
            }
        }
    }
}