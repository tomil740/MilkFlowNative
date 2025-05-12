package com.tomiappdevelopment.milk_flow.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory

actual class DatabaseFactory {
    actual fun create(): MilkFlowDb {
        val dbFile = NSHomeDirectory() + "/milk-flow.db"
        return Room.databaseBuilder<MilkFlowDb>(
            name = dbFile,
            factory = { MilkFlowDb::class.instantiateImpl() }
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}