package com.tomiappdevelopment.milk_flow.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val barcode: String,
    val name: String,
    val imageUrl: String,
    val category: String,
    val itemsPerPackage: Int,
)
