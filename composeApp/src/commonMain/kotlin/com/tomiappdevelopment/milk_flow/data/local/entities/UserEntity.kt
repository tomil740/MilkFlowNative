package com.tomiappdevelopment.milk_flow.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val name: String,
    val distributerId: String,
    val isDistributer: Boolean,
    val productsCollection: List<Int>
)
