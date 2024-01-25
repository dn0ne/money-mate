package com.dn0ne.moneymate.app.data

import com.dn0ne.moneymate.app.domain.Category
import com.dn0ne.moneymate.app.domain.DataSource
import com.dn0ne.moneymate.app.domain.ShoppingItem
import com.dn0ne.moneymate.app.domain.Spending
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

    private fun provideDataSource(): DataSource {
        return RealmDataSource(provideRealm())
    }
}