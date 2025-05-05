package com.tomiappdevelopment.milk_flow.data

import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepositoryImpl:ProductRepository {
    override suspend fun syncProductData(): Result<Boolean, Error> {
        TODO("Not yet implemented")
    }

    override suspend fun getLocalSyncMetadata(): String {
        TODO("Not yet implemented")
    }

    override suspend fun fetchRemoteSyncTimestamp(): Long {
        TODO("Not yet implemented")
    }

    override fun getProducts(): Flow<List<Product>> {
        val items1 = mutableListOf<Product>().apply {
            add(Product(1, 1, "someName1", "https://tomiappdevelopment.netlify.app/images/profileImg.JPG", "dsfsdf", 12, 2, "desc"))
            repeat(20) { i ->
                add(
                    Product(
                        i + 2,
                        1,
                        "ריקוטה 250 גרם בדץ $i",
                        "https://milkflow.netlify.app/productsImages/regular/7290005992735.webp",
                        "dsfsdf",
                        12,
                        2,
                        "desc"
                    )
                )
            }
        }
        return flow {
            emit(items1)
        }
    }
}