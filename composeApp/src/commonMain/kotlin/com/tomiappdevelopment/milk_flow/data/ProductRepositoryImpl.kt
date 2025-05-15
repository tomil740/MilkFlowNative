package com.tomiappdevelopment.milk_flow.data

import com.tomiappdevelopment.milk_flow.data.local.dao.ProductDao
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductsMetadataEntity
import com.tomiappdevelopment.milk_flow.data.remote.ProductsRemoteDataSource
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.models.ProductMetadata
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.DataException
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(private val productsDao: ProductDao,
                            private val productsRemoteDao:ProductsRemoteDataSource):ProductRepository {

    override suspend fun syncProductData(productMetadata:ProductMetadata): Result<Boolean, DataError> {
        return try {
            val products = productsRemoteDao.getAllProducts()

            productsDao.syncNewProducts(newProducts = products.map {
                ProductEntity(id=it.id, barcode = it.barcode, name = it.name, imageUrl = it.imageUrl, category = it.category, itemsPerPackage = it.itemsPerPackage)
            }, newMetadata = ProductsMetadataEntity(lastProductsUpdate = productMetadata.lastProductsUpdate, lastSyncCheckDate = productMetadata.lastSyncCheckDate))

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
            return ProductMetadata(a.lastProductsUpdate,a.lastSyncCheckDate)
        }else{
            productsDao.setMetadata(ProductsMetadataEntity(lastProductsUpdate = null, lastSyncCheckDate = null))
            return ProductMetadata()
        }
    }

    override suspend fun fetchRemoteSyncTimestamp(): Long {
        val a = productsRemoteDao.getProductsMetadata()
        return a
    }

    override fun getProducts(): Flow<List<Product>> {
        return productsDao.getAllProducts().map { prdouctEntityLst ->prdouctEntityLst.map { Product(it.id,it.barcode,it.name,it.imageUrl,it.category,it.itemsPerPackage) }  }
    }

    override suspend fun setProductLocalMetaData(productMetadata: ProductMetadata) {
        productsDao.setMetadata(
            ProductsMetadataEntity(lastProductsUpdate = productMetadata.lastProductsUpdate, lastSyncCheckDate = productMetadata.lastSyncCheckDate)
        )
    }
}