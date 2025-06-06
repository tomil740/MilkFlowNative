package com.tomiappdevelopment.milk_flow.domain.models

data class CartItem(
    val productId:Int,
    val amount:Int
)

data class CartProduct(
    val product: Product,
    val amount:Int
)