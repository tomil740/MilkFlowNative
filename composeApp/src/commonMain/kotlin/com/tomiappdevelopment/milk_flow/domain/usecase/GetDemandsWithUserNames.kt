package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetDemandsWithUserNames(
    private val demandsRepo: DemandsRepository,
    private val authRepo: AuthRepository
) {
    suspend operator fun invoke(status: Status,uid: String,isDistributor: Boolean): Flow<List<DemandWithNames>> =
        demandsRepo.getDemands(status,uid,isDistributor).map { demands ->
            demands.map { demand ->
                val userName = authRepo.getUserObjById(demand.userId)?.name ?: "Unknown"
                val distName = demand.distributerId?.let { authRepo.getUserObjById(it)?.name } ?: "N/A"
                DemandWithNames(demand, userName, distName)
            }
        }
}
