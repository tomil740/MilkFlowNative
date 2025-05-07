package com.tomiappdevelopment.milk_flow.data

import com.tomiappdevelopment.milk_flow.data.local.dao.ProductDao
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductEntity
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(private val productsDao: ProductDao):ProductRepository {

    override suspend fun syncProductData(): Result<Boolean, Error> {
        try {
            val items1 = mutableListOf<Product>().apply {
                add(Product(1, "1", "someName1", "https://tomiappdevelopment.netlify.app/images/profileImg.JPG", "dsfsdf", 12))
                repeat(20) { i ->
                    add(
                        Product(
                            i + 2,
                            "1",
                            "ריקוטה 250 גרם בדץ $i",
                            "https://milkflow.netlify.app/productsImages/regular/7290005992735.webp",
                            "dsfsdf",
                            12,
                        )
                    )
                }
            }
            productsDao.upsertAll(
                items1.map { ProductEntity(it.id,it.barcode,it.name,it.imageUrl,it.category,it.itemsPerPackage) }
            )

        }catch (e: Exception){
            return Result.Error(DataError.Local.DISK_FULL)
        }
        return Result.Success(true)
    }

    override suspend fun getLocalSyncMetadata(): String {
        TODO("Not yet implemented")
    }

    override suspend fun fetchRemoteSyncTimestamp(): Long {
        TODO("Not yet implemented")
    }

    override fun getProducts(): Flow<List<Product>> {
        return productsDao.getAllProducts().map { prdouctEntityLst ->prdouctEntityLst.map { Product(it.id,it.barcode,it.name,it.imageUrl,it.category,it.itemsPerPackage) }  }
    }
}