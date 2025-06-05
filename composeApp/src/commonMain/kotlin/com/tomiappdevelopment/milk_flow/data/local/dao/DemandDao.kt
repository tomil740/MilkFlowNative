package com.tomiappdevelopment.milk_flow.data.local.dao


import androidx.room.*
import com.tomiappdevelopment.milk_flow.data.local.entities.CartEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.DemandEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.DemandProductEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.DemandWithProductsE
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductEntity
import com.tomiappdevelopment.milk_flow.data.local.entities.ProductsMetadataEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface DemandDao {

    //sub methods

    // Upsert single DemandEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDemand(demand: DemandEntity)

    // Upsert list of DemandProductEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProducts(products: List<DemandProductEntity>)

    // Transactional upsert for a full demand + its products
    @Transaction
    suspend fun upsertFullDemand(demandWithProducts: DemandWithProductsE) {
        upsertDemand(demandWithProducts.demand)
        upsertProducts(demandWithProducts.products)
    }

    @Query("SELECT demandId FROM demands WHERE createdAt < :thresholdTime")
    suspend fun getOldDemandIds(thresholdTime: Long): List<String>

    @Query("DELETE FROM demands WHERE demandId IN (:ids)")
    suspend fun deleteDemandsByIds(ids: List<String>)

    @Query("DELETE FROM demand_products WHERE demandId IN (:ids)")
    suspend fun deleteDemandProductsByDemandIds(ids: List<String>)

    //!!!!!!!!!!!
    //exposed method!
    //!!!!!!!!!!!


    // Transactional upsert for a list of full demands + products
    @Transaction
    suspend fun upsertFullDemands(demandsWithProducts: List<DemandWithProductsE>) {
        demandsWithProducts.forEach { upsertFullDemand(it) }
    }

    @Query("SELECT * FROM demands WHERE demandId = :demandIdA")
    suspend fun getDemandById(demandIdA: String): DemandEntity?

    @Transaction
    @Query("SELECT * FROM demands WHERE demandId = :demandIdA")
    suspend fun getDemandWithProductsById(demandIdA: String): DemandWithProductsE?


    @Transaction
    @Query("SELECT * FROM demands WHERE status = :status AND uid = :uid ORDER BY updatedAt DESC")
    fun getUserDemandsWithProductsByStatusFlow(status: String, uid: String): Flow<List<DemandWithProductsE>>

    @Transaction
    @Query("SELECT * FROM demands WHERE status = :status AND distributerId = :uid ORDER BY updatedAt DESC")
    fun getDDemandsWithProductsByStatusFlow(status: String, uid: String): Flow<List<DemandWithProductsE>>

    @Transaction
    suspend fun deleteOldDemandsAndProducts(thresholdTime: Long) {
        val oldIds = getOldDemandIds(thresholdTime)
        deleteDemandProductsByDemandIds(oldIds)
        deleteDemandsByIds(oldIds)
    }

}















