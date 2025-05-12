package com.tomiappdevelopment.milk_flow.di

import androidx.room.RoomDatabase
import com.tomiappdevelopment.milk_flow.data.local.AppDatabaseConstructor
import com.tomiappdevelopment.milk_flow.data.local.MilkFlowDb
import org.koin.dsl.module

actual fun platformModule() = module {
    single<RoomDatabase.Builder<MilkFlowDb>> { AppDatabaseConstructor.initialize() }

    //single<RoomDatabase.Builder<MilkFlowDb>> { iosDatabaseBuilder() }
    //single<MilkFlowDb> { getMilkFlowDb() }
}