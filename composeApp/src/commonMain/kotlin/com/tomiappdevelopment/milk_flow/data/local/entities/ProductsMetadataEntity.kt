package com.tomiappdevelopment.milk_flow.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productsmetadata")
data class ProductsMetadataEntity(
    @PrimaryKey
    val id: Int = 0,
    val lastProductsUpdate: Long,
    val lastSyncCheckDate: String
)