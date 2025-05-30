package com.tomiappdevelopment.milk_flow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tomiappdevelopment.milk_flow.data.local.dao.ProductDao
import com.tomiappdevelopment.milk_flow.data.local.dao.UserDao
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductsMetadataEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.UserEntity

@Database(entities = [ProductEntity::class,ProductsMetadataEntity::class, UserEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MilkFlowDb : RoomDatabase(), DB {
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    override fun clearAllTables(): Unit {}
}

interface DB {
    fun clearAllTables(): Unit {}
}



