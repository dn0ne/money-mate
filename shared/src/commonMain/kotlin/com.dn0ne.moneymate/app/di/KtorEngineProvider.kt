package com.dn0ne.moneymate.app.di

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory

expect val ktorEngine: HttpClientEngineFactory<HttpClientEngineConfig>