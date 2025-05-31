package com.tomiappdevelopment.milk_flow.di

import com.tomiappdevelopment.milk_flow.data.local.DatabaseFactory
import com.tomiappdevelopment.milk_flow.data.local.MilkFlowDb
import com.tomiappdevelopment.milk_flow.data.remote.AuthService
import com.tomiappdevelopment.milk_flow.data.remote.ProductsRemoteDataSource
import com.tomiappdevelopment.milk_flow.data.remote.createHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module
import platform.Foundation.NSBundle

actual fun platformModule() = module {
     single<ProductsRemoteDataSource> {ProductsRemoteDataSource(createHttpClient(Darwin.create())) }

     val apiKey =NSBundle.mainBundle.objectForInfoDictionaryKey("FIREBASE_API_KEY") as? String ?: ""

     single<MilkFlowDb> { DatabaseFactory().create() }

     single<HttpClient> {createHttpClient(Darwin.create())}

     single<AuthService> {AuthService(client = get(),firebaseApiKey=apiKey) }

}