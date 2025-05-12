package com.tomiappdevelopment.milk_flow.di

import androidx.room.RoomDatabaseConstructor
import com.tomiappdevelopment.milk_flow.data.local.AndroidAppDatabaseConstructor
import com.tomiappdevelopment.milk_flow.data.local.AppDatabaseConstructor
import com.tomiappdevelopment.milk_flow.data.local.MilkFlowDb
import org.koin.dsl.module

actual fun platformModule() = module {
    single< RoomDatabaseConstructor<MilkFlowDb>> { AndroidAppDatabaseConstructor }

    single<MilkFlowDb> { get<AppDatabaseConstructor>().initialize() }


    //single<RoomDatabase.Builder<MilkFlowDb>> { androidDatabaseBuilder(androidContext()) }
    // single<<Room>MilkFlowDb> { androidDatabaseBuilder(androidContext()) }
}