package com.tomiappdevelopment.milk_flow.domain.repositories

import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.Demand
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.DemandError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun addItemToCart(uid: String, item: CartItem)

    suspend fun updateExistCartItem(uid: String, item: CartItem)

    suspend fun removeItemFromCart(uid: String, productId: Int)

    suspend fun clearCart(uid: String)

    suspend fun getCart(uid: String): Flow<List<CartItem>>

    suspend fun makeDemand(demand: Demand): Result<Unit, DataError.Network>
}