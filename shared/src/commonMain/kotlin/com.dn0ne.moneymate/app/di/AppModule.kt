package com.dn0ne.moneymate.app.di

import com.dn0ne.moneymate.app.data.repository.RealmSpendingRepository
import com.dn0ne.moneymate.app.domain.entities.Category
import com.dn0ne.moneymate.app.domain.entities.ShoppingItem
import com.dn0ne.moneymate.app.domain.entities.Spending
import com.dn0ne.moneymate.app.domain.repository.SpendingRepository
import com.dn0ne.moneymate.app.presentation.SpendingListViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.dsl.module

val appModule = module {
    single {
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

        Realm.open(config)
    }

    single<SpendingRepository> {
        RealmSpendingRepository(get())
    }

    viewModelDefinition {
        SpendingListViewModel(get())
    }
}