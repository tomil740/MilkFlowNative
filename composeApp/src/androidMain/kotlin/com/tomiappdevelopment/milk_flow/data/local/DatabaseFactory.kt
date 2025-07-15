package com.tomiappdevelopment.milk_flow.data.local

import android.content.Context
import androidx.room.Room
//import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create():MilkFlowDb {
        val dbFile = context.getDatabasePath("milk-flow.db")
        return Room.databaseBuilder<MilkFlowDb>(
            context = context.applicationContext,
            name = dbFile.absolutePath
        )
            //.setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(true)
            .build()
    }
}