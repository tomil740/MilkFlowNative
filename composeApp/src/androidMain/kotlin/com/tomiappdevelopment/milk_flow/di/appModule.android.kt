package com.tomiappdevelopment.milk_flow.di

import com.russhwolf.settings.Settings
import com.tomiappdevelopment.milk_flow.BuildConfig
import com.tomiappdevelopment.milk_flow.data.core.ConnectionObserverImpl
import com.tomiappdevelopment.milk_flow.data.local.AuthStorage
import com.tomiappdevelopment.milk_flow.data.local.DatabaseFactory
import com.tomiappdevelopment.milk_flow.data.local.MilkFlowDb
import com.tomiappdevelopment.milk_flow.data.local.SettingsProvider
import com.tomiappdevelopment.milk_flow.data.remote.AuthService
import com.tomiappdevelopment.milk_flow.data.remote.ProductsRemoteDataSource
import com.tomiappdevelopment.milk_flow.data.remote.createHttpClient
import com.tomiappdevelopment.milk_flow.domain.repositories.ConnectionObserver
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
actual fun platformModule() = module {

    val apiKey = BuildConfig.FIREBASE_API_KEY

    single<MilkFlowDb> { DatabaseFactory(get()).create() }

    single<HttpClient> {createHttpClient(OkHttp.create()) }

    single<ProductsRemoteDataSource> {ProductsRemoteDataSource(get()) }

    single<AuthService> {AuthService(client = get(), firebaseApiKey = apiKey) }

    single<ConnectionObserver> { ConnectionObserverImpl(get()) }
}