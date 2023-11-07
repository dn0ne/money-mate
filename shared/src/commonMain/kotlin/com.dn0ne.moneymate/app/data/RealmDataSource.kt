package com.dn0ne.moneymate.app.data

import com.dn0ne.moneymate.app.domain.Category
import com.dn0ne.moneymate.app.domain.DataSource
import com.dn0ne.moneymate.app.domain.Spending
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import org.mongodb.kbson.ObjectId

/**
 * Realm DataSource implementation
 */
class RealmDataSource(private val realm: Realm) : DataSource {
    override fun getSpendings(): Flow<List<Spending>> {
        return realm.query<Spending>().asFlow().map { it.list.reversed() }
    }

    override fun getSpendingsAfter(date: LocalDate): Flow<List<Spending>> {
        TODO("Not yet implemented")
    }

    override fun getSpendingsWithCategory(category: Category): Flow<List<Spending>> {
        return realm.query<Spending>(query = "category == $0", category).asFlow().map { it.list }
    }

    override fun getSpendingsWithCategoryAfter(
        category: Category,
        date: LocalDate
    ): Flow<List<Spending>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertSpending(spending: Spending) {
        realm.write {
            spending.category = findLatest(spending.category!!)
            copyToRealm(instance = spending)
        }
    }

    override suspend fun updateSpending(spending: Spending) {
        realm.write {
            val queriedSpending =
                realm.query<Spending>(query = "_id == $0", spending.id).first().find()
            queriedSpending?.category = spending.category
            queriedSpending?.amount = spending.amount
            queriedSpending?.shortDescription = spending.shortDescription
            queriedSpending?.shoppingList = spending.shoppingList
        }
    }

    override suspend fun deleteSpending(id: ObjectId) {
        realm.write {
            val queriedSpending = query<Spending>(query = "_id == $0", id).first().find()
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

    override suspend fun deleteCategory(id: ObjectId) {
        TODO("Not yet implemented")
    }

}