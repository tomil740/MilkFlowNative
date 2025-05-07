package com.tomiappdevelopment.milk_flow.di

import com.tomiappdevelopment.milk_flow.data.local.MilkFlowDb
import com.tomiappdevelopment.milk_flow.data.local.getMilkFlowDb
import org.koin.dsl.module

actual fun platformModule() = module {
    single<MilkFlowDb> { getMilkFlowDb() }
}