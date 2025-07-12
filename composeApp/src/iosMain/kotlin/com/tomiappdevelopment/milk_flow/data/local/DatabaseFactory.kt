package com.tomiappdevelopment.milk_flow.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent

actual class DatabaseFactory {
    actual fun create(): MilkFlowDb {
        val fileManager = NSFileManager.defaultManager
 
        val documentsDirectory = fileManager
            .URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
            .first() as? NSURL
            ?: error("Unable to access documents directory")

        val dbUrl = documentsDirectory.URLByAppendingPathComponent("milk-flow.db")
        val dbPath = dbUrl?.path ?: error("Could not get DB path")

        return Room.databaseBuilder<MilkFlowDb>(
            name = dbPath,
            factory = { MilkFlowDb::class.instantiateImpl() }
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.Default)
            .fallbackToDestructiveMigration(true)
            .build()

    }
}
