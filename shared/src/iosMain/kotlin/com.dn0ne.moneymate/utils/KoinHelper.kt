package com.dn0ne.moneymate.utils

import com.dn0ne.moneymate.app.di.appModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}