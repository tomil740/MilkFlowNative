package com.tomiappdevelopment.milk_flow.data.local.entities

import androidx.room.Entity

@Entity(
    tableName = "cart",
    primaryKeys = ["uid", "productId"]
)
data class CartEntity(
    val uid: String,           // Cart owner (user ID)
    val productId: Int,        // Unique product
    val amount: Int            // Selected amount for that product
)
