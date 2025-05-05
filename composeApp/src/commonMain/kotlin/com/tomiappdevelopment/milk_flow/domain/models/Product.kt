package com.tomiappdevelopment.milk_flow.domain.models

data class Product(
    val id: Int,
    val barcode: Int,
    val name: String,
    val imgKey: String,
    val category: String,
    val itemsPerPackage: Int,
    val weight: Int,
    val description: String,
)
