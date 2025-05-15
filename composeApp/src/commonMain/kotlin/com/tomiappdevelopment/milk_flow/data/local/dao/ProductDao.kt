package com.tomiappdevelopment.milk_flow.data.local.dao

import androidx.room.*
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductsMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Transaction
    suspend fun syncNewProducts(
        newProducts: List<ProductEntity>,
        newMetadata: ProductsMetadataEntity
    ) {
        deleteAll()
        clearMetadata()

        upsertAll(newProducts)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("SELECT * FROM products_metadata WHERE id = 0")
    suspend fun getMetadata(): ProductsMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setMetadata(metadata: ProductsMetadataEntity)

    @Query("DELETE FROM products_metadata")
    suspend fun clearMetadata()
}
