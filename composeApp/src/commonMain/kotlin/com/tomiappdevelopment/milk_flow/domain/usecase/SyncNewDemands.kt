package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.domain.models.Demand
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import com.tomiappdevelopment.milk_flow.domain.util.DemandError
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.domain.util.toDemandError

class SyncNewDemands(
    private val repo: DemandsRepository
) {

    suspend fun invoke(uid: String, isDistributor: Boolean): Result<Boolean, DemandError> {
        val basePageSize = 20
        var currentPage = 0
        var anyNewData = false

        try {
            while (currentPage < MAX_PAGES) {
                val pageSize = basePageSize * (currentPage + 1)

                val result = repo.fetchNewPage(pageSize, uid, isDistributor)
                val page = when (result) {
                    is Result.Success -> result.data
                    is Result.Error -> return Result.Error(result.error.toDemandError())
                }

                if (page.demands.isEmpty()) break

                val isMostlyNew = isNewData(page.demands)
                if (isMostlyNew) {
                    anyNewData = true
                }

                repo.upsertDemandsList(page.demands)

                // Since no real pagination token, we rely purely on isNewData
                if (!isMostlyNew) break

                currentPage++
            }

            return Result.Success(anyNewData)
        } catch (e: Exception) {
            return Result.Error(DemandError.Unknown)
        }
    }

    private suspend fun isNewData(newData: List<Demand>): Boolean {
        var newCount = 0

        for (item in newData) {
            val local = repo.getDemandById(item.id)
            if (local == null || local.status != item.status) {
                newCount++
            }
        }

        val ratio = newCount.toDouble() / newData.size
        return ratio >= 0.3
    }

    companion object {
        private const val MAX_PAGES = 100
    }
}
