package com.tomiappdevelopment.milk_flow.di

import com.tomiappdevelopment.milk_flow.data.local.DatabaseFactory
import com.tomiappdevelopment.milk_flow.data.local.MilkFlowDb
import com.tomiappdevelopment.milk_flow.data.remote.ProductsRemoteDataSource
import com.tomiappdevelopment.milk_flow.data.remote.createHttpClient
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

actual fun platformModule() = module {
     single<ProductsRemoteDataSource> {ProductsRemoteDataSource(createHttpClient(Darwin.create())) }


     single<MilkFlowDb> { DatabaseFactory().create() }
}