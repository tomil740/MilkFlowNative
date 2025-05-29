package com.tomiappdevelopment.milk_flow.data.local.dao

import androidx.room.*
import com.tomiappdevelopment.milk_flow.data.local.entities.CartEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductsMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // Add item from marketplace (merge if exists)
    @Transaction
    suspend fun addOrMergeItem(uid: String, productId: Int, amount: Int) {
        val existing = getItem(uid, productId)
        if (existing != null) {
            upsert(
                CartEntity(
                    uid = uid,
                    productId = productId,
                    amount = existing.amount + amount
                )
            )
        } else {
            upsert(CartEntity(uid = uid, productId = productId, amount = amount))
        }
    }

    // Get specific item
    @Query("SELECT * FROM cart WHERE uid = :uid AND productId = :productId")
    suspend fun getItem(uid: String, productId: Int): CartEntity?

    // Replace (for update from cart screen)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CartEntity)

    // Delete specific item (from cart screen)
    @Query("DELETE FROM cart WHERE uid = :uid AND productId = :productId")
    suspend fun deleteItem(uid: String, productId: Int)

    // Clear full cart (on submit or screen action)
    @Query("DELETE FROM cart WHERE uid = :uid")
    suspend fun clearCart(uid: String)

    // Observe all items for a user
    @Query("SELECT * FROM cart WHERE uid = :uid")
    fun observeCart(uid: String): Flow<List<CartEntity>>
}
