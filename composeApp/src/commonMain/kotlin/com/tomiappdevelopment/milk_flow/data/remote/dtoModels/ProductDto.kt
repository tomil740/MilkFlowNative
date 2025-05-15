package com.tomiappdevelopment.milk_flow.data.remote.dtoModels

data class ProductDto(
    val id: Int,
    val barcode: String,
    val name: String,
    val imageUrl: String,
    val category: String,
    val itemsPerPackage: Int,
)
