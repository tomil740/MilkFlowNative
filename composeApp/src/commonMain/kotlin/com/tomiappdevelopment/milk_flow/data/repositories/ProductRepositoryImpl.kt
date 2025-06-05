package com.tomiappdevelopment.milk_flow.data.repositories

import com.tomiappdevelopment.milk_flow.data.local.dao.ProductDao
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductsMetadataEntity
import com.tomiappdevelopment.milk_flow.data.remote.ProductsRemoteDataSource
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.ProductDto
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.models.ProductMetadata
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.DataException
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(private val productsDao: ProductDao,
                            private val productsRemoteDao: ProductsRemoteDataSource
): ProductRepository {

    override suspend fun syncProductData(productMetadata: ProductMetadata): Result<Boolean, DataError> {
        return try {
            val products = productsRemoteDao.getAllProducts()
            when(products){
                is Result.Error<DataError.Network> -> Result.Error(products.error)
                is Result.Success<List<ProductDto>> -> {
                    productsDao.syncNewProducts(newProducts = products.data.map {
                        ProductEntity(
                            id = it.id.toInt(),
                            barcode = it.barcode.toString(),
                            name = it.name,
                            imageUrl = it.imgKey,
                            category = it.category,
                            itemsPerPackage = it.itemsPerPackage.toInt()
                        )
                    }, newMetadata = ProductsMetadataEntity(
                        lastProductsUpdate = productMetadata.lastProductsUpdate,
                        lastSyncCheckDate = productMetadata.lastSyncCheckDate
                    )
                    )
                }

            }
            Result.Success(true)
        } catch (e: DataException) {
            Result.Error(e.error)
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }

    override suspend fun getLocalMetadata(): ProductMetadata {
        val a = productsDao.getMetadata()
        if (a!= null){
            return ProductMetadata(a.lastProductsUpdate, a.lastSyncCheckDate)
        }else{
            setProductLocalMetaData(
                ProductMetadata(
                    lastProductsUpdate = 1L,
                    lastSyncCheckDate = "null"
                )
            )
            return ProductMetadata()
        }
    }

    override suspend fun fetchRemoteSyncTimestamp(): Long {
        val a = productsRemoteDao.getProductsMetadata()
        return when(a){
            is Result.Error<*> -> {
                println("the obj ${a.error}")
                 -1
            }
            is Result.Success<*> -> {
                println("the obj ${a.data}")
                a.data.toString().toLong()
            }
        }

    }

    override fun getProducts(): Flow<List<Product>> {
        return productsDao.getAllProducts().map { prdouctEntityLst ->prdouctEntityLst.map {
            Product(
                it.id,
                it.barcode,
                it.name,
                it.imageUrl,
                it.category,
                it.itemsPerPackage
            )
        }  }
    }

    override suspend fun setProductLocalMetaData(productMetadata: ProductMetadata) {

        productsDao.setMetadata(
            ProductsMetadataEntity(
                lastProductsUpdate = productMetadata.lastProductsUpdate,
                lastSyncCheckDate = productMetadata.lastSyncCheckDate
            )
        )
    }

    override suspend fun getProductsByIds(ids: List<Int>): List<Product> {
        return productsDao.getByIds(ids).map {
            Product(
                it.id,
                it.barcode,
                it.name,
                it.imageUrl,
                it.category,
                it.itemsPerPackage
            )
        }
    }
}