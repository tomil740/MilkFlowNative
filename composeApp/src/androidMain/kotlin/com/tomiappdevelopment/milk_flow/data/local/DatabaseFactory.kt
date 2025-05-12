package com.tomiappdevelopment.milk_flow.data.local
import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers


@SuppressLint("StaticFieldLeak")
actual object AppDatabaseConstructor : RoomDatabaseConstructor<MilkFlowDb> {
    lateinit var context: Context

    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

    actual override fun initialize(): MilkFlowDb {
        val dbFile = context.getDatabasePath("milk_flow.db")
        return Room.databaseBuilder(
            context,
            MilkFlowDb::class.java,
            dbFile.absolutePath
        ).build()
    }
}

/*

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create():MilkFlowDb {
        val dbFile = context.getDatabasePath("milk-flow.db")
        return Room.databaseBuilder<MilkFlowDb>(
            context = context.applicationContext,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

 */
/*
fun androidDatabaseBuilder(context: Context): RoomDatabase.Builder<MilkFlowDb>{
    val dbFile = context.applicationContext.getDatabasePath("milk_flow.db")

    return Room.databaseBuilder(
        context, dbFile.absolutePath
    )
}

 */



