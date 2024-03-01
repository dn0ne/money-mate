package com.dn0ne.moneymate.app.data

import com.dn0ne.moneymate.app.domain.entities.Category
import com.dn0ne.moneymate.app.domain.entities.ShoppingItem
import com.dn0ne.moneymate.app.domain.entities.Spending
import com.dn0ne.moneymate.app.domain.repository.SpendingRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

/**
 * Data source provider object
 */
object DatabaseModule {
    val dataSource = provideDataSource()
    private fun provideRealm(): Realm {
        // Creating Realm configuration
        val config = RealmConfiguration.Builder(
            schema = setOf(
                Spending::class,
                Category::class,
                ShoppingItem::class
            )
        )
            .compactOnLaunch()
            .build()
        return Realm.open(config)
    }

    private fun provideDataSource(): SpendingRepository {
        return RealmSpendingRepository(provideRealm())
    }
}