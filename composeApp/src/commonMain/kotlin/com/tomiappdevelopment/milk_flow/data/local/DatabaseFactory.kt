package com.tomiappdevelopment.milk_flow.data.local

import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
/*
/*
expect class DatabaseFactory {
    fun create(): MilkFlowDb
}

 */
class CreateDatabase(private val builder: RoomDatabase.Builder<MilkFlowDb>){

    fun getDatabase(): MilkFlowDb{
        return builder
            .fallbackToDestructiveMigration(dropAllTables = true)
            .setDriver (BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO).build()
    }

}

 */
// The Room compiler generates the `actual` implementations.
expect object AppDatabaseConstructor : RoomDatabaseConstructor<MilkFlowDb> {
    override fun initialize(): MilkFlowDb
}


