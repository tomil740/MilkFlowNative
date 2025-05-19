package com.tomiappdevelopment.milk_flow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tomiappdevelopment.milk_flow.data.local.dao.ProductDao
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductsMetadataEntity

@Database(entities = [ProductEntity::class,ProductsMetadataEntity::class], version = 3, exportSchema = false)
abstract class MilkFlowDb : RoomDatabase(), DB {
    abstract fun productDao(): ProductDao
    override fun clearAllTables(): Unit {}
}

interface DB {
    fun clearAllTables(): Unit {}
}



