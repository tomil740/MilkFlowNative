package com.tomiappdevelopment.milk_flow.data.remote.dtoModels


data class ProductDto(
    val id: Int,
    val barcode: Long,
    val category: String,
    val description: String = "",
    val imgKey: String,
    val itemsPerPackage: Int,
    val name: String,
    val weight: Int = 0
)
