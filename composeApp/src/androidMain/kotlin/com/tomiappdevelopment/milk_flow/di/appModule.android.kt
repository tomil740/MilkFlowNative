package com.tomiappdevelopment.milk_flow.di

import com.tomiappdevelopment.milk_flow.data.local.DatabaseFactory
import com.tomiappdevelopment.milk_flow.data.local.MilkFlowDb
import org.koin.dsl.module

actual fun platformModule() = module {
    single<MilkFlowDb> { DatabaseFactory(get()).create() }
}