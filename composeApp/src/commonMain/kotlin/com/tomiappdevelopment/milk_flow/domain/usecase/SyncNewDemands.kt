package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.domain.models.Demand
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import com.tomiappdevelopment.milk_flow.domain.util.Result
class SyncNewDemands(
    private val repo: DemandsRepository
) {

    suspend fun invoke(uid: String,isDistributor: Boolean) {
        var nextPageToken: String? =null
        var pagesFetched = 0

        try {
            do {
                val result = repo.fetchNewPage(nextPageToken,uid,isDistributor)

                val page = when (result) {
                    is Result.Success -> result.data
                    is Result.Error -> {
                        // Log or handle error here if needed
                        return
                    }
                }

                if (page.demands.isEmpty()) break

                val isMostlyNew = isNewData(page.demands)

                repo.upsertDemandsList(page.demands)

                nextPageToken = page.nextPageToken
                pagesFetched++

            } while (isMostlyNew && nextPageToken != null && pagesFetched < MAX_PAGES)
        } catch (e: Exception) {
            // Optional: log or handle exceptions
        }
    }

    private suspend fun isNewData(newData: List<Demand>): Boolean {
        var newCount = 0

        for (item in newData) {
            val local = repo.getDemandById(item.id)

            if (local == null) {
                newCount++
                continue
            }

            if (local.status != item.status) {
                newCount++
            }
        }

        val ratio = newCount.toDouble() / newData.size
        return ratio >= 0.3
    }

    companion object {
        private const val MAX_PAGES = 100 // safety limit to prevent infinite loops
    }
}
