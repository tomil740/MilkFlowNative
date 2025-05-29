package com.tomiappdevelopment.milk_flow.data.repositories

import com.tomiappdevelopment.milk_flow.data.local.dao.CartDao
import com.tomiappdevelopment.milk_flow.data.local.entities.CartEntity
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.repositories.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepositoryImpl(
    private val cartDao: CartDao
): CartRepository {

    override suspend fun addItemToCart(
        uid: String,
        item: CartItem
    ) {
        cartDao.addOrMergeItem(
            uid=uid,
            productId = item.productId,
            amount = item.amount
        )
    }

    override suspend fun updateExistCartItem(
        uid: String,
        item: CartItem
    ) {
        val a = CartEntity(
            uid = uid,
            productId = item.productId,
            amount = item.amount
        )
        cartDao.upsert(
            a
        )
    }

    override suspend fun removeItemFromCart(uid: String, productId: Int) {
        cartDao.deleteItem(
            uid=uid,
            productId=productId
        )
    }

    override suspend fun clearCart(uid: String) {
        cartDao.clearCart(
            uid
        )
    }

    override suspend fun getCart(uid: String): Flow<List<CartItem>> {
        return  cartDao.observeCart(
            uid
        ).map { res->
            res.map {
                CartItem(
                    productId = it.productId,
                    amount = it.amount
                )
            }
        }
    }


}