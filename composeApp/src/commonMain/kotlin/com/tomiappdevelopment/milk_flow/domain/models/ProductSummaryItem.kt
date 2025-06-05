package com.tomiappdevelopment.milk_flow.domain.models

data class ProductSummaryItem(
    val productName: String,
    val productId: Int,
    val barcode: String,
    val productImgUrl: String,
    val usersDemand: List<UserProductDemand>,
    val amountSum:Int
)
data class UserProductDemand(
    val userName: String,
    val amount: Int
)