package com.tomiappdevelopment.milk_flow.data.remote.dtoModels

import androidx.room.PrimaryKey

data class UserDto(
    val uid: String,
    val name: String,
    val distributerId: String,
    val isDistributer: Boolean,
    val productsCollection: List<Int>
)
