package com.tomiappdevelopment.milk_flow.domain.repositories

import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.Demand
import com.tomiappdevelopment.milk_flow.domain.models.DemandsWithNextPageToken
import com.tomiappdevelopment.milk_flow.domain.models.subModels.DemandStatusUpdateEntry
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface DemandsRepository {

    // Fetch a page of demands from remote API within last 72 hours,
    // ordered by updatedAt, paginated by page number
    suspend fun fetchNewPage(pageToken: String?,uid: String,isDistributor: Boolean): Result<DemandsWithNextPageToken, DataError.Network>

    // Get a single demand by its ID from local DB
    suspend fun getDemandById(demandId: String): Demand?

    // Upsert (insert or update) a list of demands into local DB
    suspend fun upsertDemandsList(demandsList: List<Demand>)

    // Optional: delete demands older than certain timestamp (for cleanup)
    suspend fun cleanOldDemands(cutoffTimestamp: Long)

    // Get a single demand by its ID from local DB
    suspend fun getDemands(status: Status,uid: String,isDistributor: Boolean): Flow<List<Demand>>

    suspend fun updateDemandsStatus(params: DemandStatusUpdateEntry): Result<Unit, DataError.Network>

}
