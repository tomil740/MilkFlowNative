package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import com.tomiappdevelopment.milk_flow.domain.util.DemandError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.domain.util.toDemandError
import kotlinx.coroutines.CancellationException

class SyncNewDemands(
    private val repo: DemandsRepository
) {

    suspend fun invoke(uid: String, isDistributor: Boolean): Result<Boolean, DemandError> {
        return try {
            when (val result = repo.syncDemandsData(uid, isDistributor)) {
                is Result.Error -> Result.Error(result.error.toDemandError())
                is Result.Success -> {
                    val demands = result.data.demands
                    repo.upsertDemandsList(demands)
                    val hasNewDelete = demands.any { it.status == Status.deleted }
                    Result.Success(hasNewDelete)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(DemandError.Unknown)
        }
    }
}
