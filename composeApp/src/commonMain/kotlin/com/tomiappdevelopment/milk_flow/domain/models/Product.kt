package com.tomiappdevelopment.milk_flow.domain.models

import com.tomiappdevelopment.milk_flow.domain.core.ImageDefaults

data class Product(
    val id: Int,
    val barcode: String,
    val name: String,
    val imageBaseId: String,
    val category: String,
    val itemsPerPackage: Int,
) {
    fun effectiveImageUrl(): String =
        "${ImageDefaults.basicProductUrl}$imageBaseId.webp"
}
