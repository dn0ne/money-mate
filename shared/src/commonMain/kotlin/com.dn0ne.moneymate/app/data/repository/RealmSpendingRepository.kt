package com.dn0ne.moneymate.app.data.repository

import com.dn0ne.moneymate.app.domain.entities.spending.Category
import com.dn0ne.moneymate.app.domain.entities.spending.Spending
import com.dn0ne.moneymate.app.domain.repository.SpendingRepository
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
class RealmSpendingRepository(private val realm: Realm) : SpendingRepository {
    override fun getSpendings(): Flow<List<Spending>> {
        return realm.query<Spending>().asFlow().map { it.list.reversed() }
    }

    override suspend fun insertSpending(spending: Spending) {
        realm.write {
            val queriedCategory = spending.category?.let {
                realm.query<Category>(query = "id == $0", it.id).first().find()
            }

            queriedCategory?.let { category ->
                spending.category = findLatest(category)
                copyToRealm(instance = spending)
            }
        }
    }

    override suspend fun updateSpending(spending: Spending) {
        realm.write {
            val queriedSpending = realm.query<Spending>(
                query = "id == $0", spending.id
            ).first().find()?.let { spending ->
                findLatest(spending)
            }

            val queriedCategory = realm.query<Category>(
                query = "id == $0", spending.category!!.id
            ).first().find()

            queriedCategory?.let { category ->
                queriedSpending?.apply {
                    this.category = findLatest(category)
                    amount = spending.amount
                    shortDescription = spending.shortDescription
                    shoppingList = spending.shoppingList
                }
            }
        }
    }

    override suspend fun deleteSpending(id: ObjectId) {
        realm.write {
            val queriedSpending = query<Spending>(query = "id == $0", id).first().find()
            try {
                queriedSpending?.let { spending -> delete(spending) }
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
            val queriedCategory = realm.query<Category>(
                query = "id == $0", category.id
            ).first().find()?.let { category ->
                findLatest(category)
            }

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