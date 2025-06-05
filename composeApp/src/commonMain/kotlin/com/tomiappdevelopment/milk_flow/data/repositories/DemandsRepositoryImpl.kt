package com.tomiappdevelopment.milk_flow.data.repositories

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
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DemandsRepositoryImpl(
    private val demandsRemoteDao: DemandsRemoteDao,
    private val demandsDao: DemandDao
): DemandsRepository {
    override suspend fun fetchNewPage(pageToken: String?,uid: String,isDistributor: Boolean): Result<DemandsWithNextPageToken, DataError.Network> {
         val fetchedData = demandsRemoteDao.getDemandsPage(pageToken)
       return when(fetchedData){
            is Result.Error<DataError.Network> -> return fetchedData
            is Result.Success<PagedDemandsDto> -> {
                // Transform the PagedDemandsDto to DemandsWithNextPageToken

                val userDemands = if(isDistributor){fetchedData.data.demands.filter { it.distributerId==uid }}else{
                    fetchedData.data.demands.filter { it.userId==uid}
                }
                val demandsWithNextPageToken = DemandsWithNextPageToken(
                    demands = userDemands.map { it.toDemand() },
                    nextPageToken = fetchedData.data.nextPageToken
                )
                Result.Success(demandsWithNextPageToken)
            }
        }

    }

    override suspend fun getDemandById(demandId: String): Demand? {
        val a = demandsDao.getDemandById(demandId)
        if (a!=null){
           return Demand(
                id = a.demandId,
                userId = a.uid,
                a.distributerId,
                Status.valueOf(a.status),
                a.createdAt.toLocalDateTime(),
                a.updatedAt.toLocalDateTime(),
                products = listOf()
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

    override suspend fun getDemands(status: Status,uid: String,isDistributor: Boolean): Flow<List<Demand>> {
        val theDaoFun = if(isDistributor){ demandsDao.getDDemandsWithProductsByStatusFlow(status.toString(),uid)
        }else{ demandsDao.getUserDemandsWithProductsByStatusFlow(status.toString(),uid)}
        return theDaoFun.map { data->
            data.map { obj->
                Demand(
                    obj.demand.demandId,
                    obj.demand.uid,
                    obj.demand.distributerId,
                    Status.valueOf(obj.demand.status),
                    createdAt = obj.demand.createdAt.toLocalDateTime(),
                    updatedAt = obj.demand.updatedAt.toLocalDateTime(),
                    products = obj.products.map { CartItem(it.productId,it.amount) }
                )
            }

        }
    }

}