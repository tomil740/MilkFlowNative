package com.tomiappdevelopment.milk_flow.data.repositories

import com.tomiappdevelopment.milk_flow.data.local.DemandSyncStore
import com.tomiappdevelopment.milk_flow.data.local.dao.DemandDao
import com.tomiappdevelopment.milk_flow.data.local.entities.toDemandWithProductsE
import com.tomiappdevelopment.milk_flow.data.remote.DemandsRemoteDao
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.PagedDemandsDto
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.toDemand
import com.tomiappdevelopment.milk_flow.data.util.toLocalDateTime
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.Demand
import com.tomiappdevelopment.milk_flow.domain.models.DemandsWithNextPageToken
import com.tomiappdevelopment.milk_flow.domain.models.subModels.DemandStatusUpdateEntry
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class DemandsRepositoryImpl(
    private val demandsRemoteDao: DemandsRemoteDao,
    private val demandsDao: DemandDao
): DemandsRepository {
    override suspend fun fetchNewPage(page: Int?, uid: String, isDistributor: Boolean): Result<DemandsWithNextPageToken, DataError.Network> {
        val sinceTimestamp = DemandSyncStore.getDemandsLastSync()
        val fetchedData = demandsRemoteDao.getDemandsPage(sinceTimestamp)
        return when (fetchedData) {
            is Result.Error -> fetchedData
            is Result.Success -> {
                DemandSyncStore.setDemandsLastSync()
                Result.Success(
                    DemandsWithNextPageToken(
                        demands = fetchedData.data.demands.map { it.toDemand() },
                        nextPageToken = fetchedData.data.nextPageToken
                    )
                )
            }
        }
    }

    override suspend fun getDemandById(demandId: String): Demand? {
        val b = demandsDao.getDemandWithProductsById(demandId)
        if (b!=null){
            val a = b.demand
           return Demand(
                id = a.demandId,
                userId = a.uid,
                a.distributerId?: "",
                Status.valueOf(a.status),
                a.createdAt.toLocalDateTime(),
                a.updatedAt.toLocalDateTime(),
               products = b.products.map { CartItem(it.productId,it.amount) }
            )
        }

        return null
    }

    override suspend fun upsertDemandsList(demandsList: List<Demand>) {
        demandsDao.upsertFullDemands(
            demandsList.map { it.toDemandWithProductsE() }
        )
    }

    override suspend fun cleanOldDemands(cutoffTimestamp: Long) {
        demandsDao.deleteOldDemandsAndProducts(cutoffTimestamp)
    }

    override suspend fun getDemands(
        status: Status,
        uid: String,
        isDistributor: Boolean
    ): Flow<List<Demand>> {
        val theDaoFun = if (isDistributor) {
            demandsDao.getDemandsWithProductsByStatusFlow(status.toString(), uid)
        } else {
            demandsDao.getUserDemandsWithProductsByStatusFlow(status.toString(), uid)
        }

        return theDaoFun.map { data ->
            data
                // Add safety check: filter out objects with wrong UID/distributorId
                .filter { obj ->
                    if (isDistributor) {
                        obj.demand.distributerId == uid
                    } else {
                        obj.demand.uid == uid
                    }
                }
                .map { obj ->
                    Demand(
                        obj.demand.demandId,
                        obj.demand.uid,
                        obj.demand.distributerId ?: "",
                        Status.valueOf(obj.demand.status),
                        createdAt = obj.demand.createdAt.toLocalDateTime(),
                        updatedAt = obj.demand.updatedAt.toLocalDateTime(),
                        products = obj.products.map { CartItem(it.productId, it.amount) }
                    )
                }
        }
    }


    override suspend fun updateDemandsStatus(params: DemandStatusUpdateEntry): Result<Unit, DataError.Network> {
        return demandsRemoteDao.updateDemandsStatus(params)
    }

}