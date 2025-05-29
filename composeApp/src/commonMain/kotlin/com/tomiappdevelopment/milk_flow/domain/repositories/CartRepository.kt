package com.tomiappdevelopment.milk_flow.domain.repositories

import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun addItemToCart(uid: String, item: CartItem)

    suspend fun updateExistCartItem(uid: String, item: CartItem)

    suspend fun removeItemFromCart(uid: String, productId: Int)

    suspend fun clearCart(uid: String)

    suspend fun getCart(uid: String): Flow<List<CartItem>>
}