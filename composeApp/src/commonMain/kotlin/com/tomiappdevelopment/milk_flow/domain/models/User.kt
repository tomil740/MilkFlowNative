package com.tomiappdevelopment.milk_flow.domain.models

data class User(
    val name: String,
    val uid: String,
    val distributerId: String?,
    val isDistributer: Boolean,
    val productsCollection: List<Int>
)