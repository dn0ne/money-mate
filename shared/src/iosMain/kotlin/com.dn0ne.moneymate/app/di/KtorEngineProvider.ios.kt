package com.dn0ne.moneymate.app.di

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual val ktorEngine: HttpClientEngineFactory<HttpClientEngineConfig> = Darwin