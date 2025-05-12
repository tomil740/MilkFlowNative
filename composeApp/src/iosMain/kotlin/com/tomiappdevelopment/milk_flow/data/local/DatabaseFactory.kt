package com.tomiappdevelopment.milk_flow.data.local

import androidx.room.Room
import androidx.room.RoomDatabaseConstructor
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/*
fun getMilkFlowDb(): MilkFlowDb {
    val dbFile = NSHomeDirectory() + "/milk_flow.db"
    return Room.databaseBuilder<MilkFlowDb>(
        name = dbFile,
        factory = { MilkFlowDb::class.instantiateImpl() }
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

 */

actual object AppDatabaseConstructor : RoomDatabaseConstructor<MilkFlowDb> {
    actual override fun initialize(): MilkFlowDb {
        val dbFilePath = documentDirectory() + "/milk_flow.db"
        return Room.databaseBuilder<MilkFlowDb>(
            dbFilePath
        ).build()
    }
}

/*
fun iosDatabaseBuilder(): RoomDatabase.Builder<MilkFlowDb>{
    val dbFilePath = documentDirectory() + "/milk_flow.db"
    return Room.databaseBuilder<MilkFlowDb>(
        dbFilePath)
}

 */

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return requireNotNull(documentDirectory?.path)
}