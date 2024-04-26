package com.dn0ne.moneymate.app.di

import com.dn0ne.moneymate.app.data.remote.SyncApi
import com.dn0ne.moneymate.app.data.repository.RealmChangeRepository
import com.dn0ne.moneymate.app.data.repository.RealmSpendingRepository
import com.dn0ne.moneymate.app.data.repository.SyncRepository
import com.dn0ne.moneymate.app.domain.entities.change.Change
import com.dn0ne.moneymate.app.domain.entities.spending.Category
import com.dn0ne.moneymate.app.domain.entities.spending.ShoppingItem
import com.dn0ne.moneymate.app.domain.entities.spending.Spending
import com.dn0ne.moneymate.app.domain.repository.ChangeRepository
import com.dn0ne.moneymate.app.domain.repository.SpendingRepository
import com.dn0ne.moneymate.app.domain.sync.SyncService
import com.dn0ne.moneymate.app.presentation.SpendingListViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

val appModule = module {
    single {
        // Creating Realm configuration
        val config = RealmConfiguration.Builder(
            schema = setOf(
                Spending::class,
                Category::class,
                ShoppingItem::class,
                Change.InsertSpending::class,
                Change.UpdateSpending::class,
                Change.DeleteSpending::class,
                Change.InsertCategory::class,
                Change.UpdateCategory::class,
                Change.DeleteCategory::class
            )
        )
            .compactOnLaunch()
            .build()

        Realm.open(config)
    }

    single<HttpClient> {
        HttpClient(ktorEngine) {
            install(HttpTimeout) {
                socketTimeoutMillis = 180_000
                requestTimeoutMillis = 180_000
            }
            defaultRequest {
                url("https://money-mate-server-xd0c.onrender.com/api/")
            }
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                    }
                )
            }
            expectSuccess = false
        }
    }

    single<ChangeRepository> {
        RealmChangeRepository(realm = get())
    }

    single<SpendingRepository>(qualifier = qualifier(SpendingRepoImplementation.DEFAULT)) {
        RealmSpendingRepository(realm = get())
    }

    single<SpendingRepository>(qualifier = qualifier(SpendingRepoImplementation.SYNC)) {
        SyncRepository(
            spendingRepository = get(qualifier(SpendingRepoImplementation.DEFAULT)),
            changeRepository = get()
        )
    }

    single {
        SyncApi(client = get())
    }

    single {
        SyncService(
            spendingRepository = get(qualifier(SpendingRepoImplementation.DEFAULT)),
            changeRepository = get(),
            syncApi = get()
        )
    }

    viewModelDefinition {
        SpendingListViewModel(
            repository = get(qualifier(SpendingRepoImplementation.SYNC)),
            syncService = get()
        )
    }


}

enum class SpendingRepoImplementation {
    DEFAULT, SYNC
}