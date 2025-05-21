package com.tomiappdevelopment.milk_flow.domain.models

data class Product(
    val id: Int,
    val barcode: String,
    val name: String,
    val imageUrl: String,
    val category: String,
    val itemsPerPackage: Int,
)
