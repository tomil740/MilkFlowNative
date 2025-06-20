package com.tomiappdevelopment.milk_flow.domain.models

import com.tomiappdevelopment.milk_flow.domain.core.ImageDefaults

data class ProductSummaryItem(
    val productName: String,
    val productId: Int,
    val barcode: String,
    val productImgUrl: String,
    val usersDemand: List<UserProductDemand>,
    val amountSum:Int
){
    fun effectiveImageUrl(): String =
        "${ImageDefaults.basicProductUrl}$productImgUrl.webp"
}

data class UserProductDemand(
    val userName: String,
    val amount: Int
)