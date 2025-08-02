package com.tomiappdevelopment.milk_flow.di

import com.tomiappdevelopment.milk_flow.data.core.ConnectionObserverImpl
import com.tomiappdevelopment.milk_flow.data.local.AuthStorageImpl
import com.tomiappdevelopment.milk_flow.data.local.DatabaseFactory
import com.tomiappdevelopment.milk_flow.data.local.MilkFlowDb
import com.tomiappdevelopment.milk_flow.data.local.SettingsProvider
import com.tomiappdevelopment.milk_flow.data.remote.AuthService
import com.tomiappdevelopment.milk_flow.data.remote.ProductsRemoteDataSource
import com.tomiappdevelopment.milk_flow.data.remote.createHttpClient
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthStorage
import com.tomiappdevelopment.milk_flow.domain.repositories.ConnectionObserver
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSBundle

actual fun platformModule() = module {
     single<ProductsRemoteDataSource> {ProductsRemoteDataSource(createHttpClient(Darwin.create())) }

     val apiKey =NSBundle.mainBundle.objectForInfoDictionaryKey("FIREBASE_API_KEY") as? String ?: ""

     single<MilkFlowDb> { DatabaseFactory().create() }

     single<HttpClient> {createHttpClient(Darwin.create())}

     single<AuthService> {AuthService(client = get(),firebaseApiKey=apiKey) }

     single<ConnectionObserver> {ConnectionObserverImpl(get())}

     single<com.russhwolf.settings.Settings> { SettingsProvider.settings }

     single { AuthStorageImpl(get()) }.bind(AuthStorage::class)

}