package com.tomiappdevelopment.milk_flow.domain.repositories

import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.domain.util.Error
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
//those 3 are used by the use case algortem to make sync with minimum resources
    suspend fun syncProductData(): Result<Boolean, Error>

    suspend fun getLocalSyncMetadata(): String//define object when implementing the table(on web version simple timestamp )

    suspend fun fetchRemoteSyncTimestamp(): Long

    fun getProducts(): Flow<List<Product>>

}