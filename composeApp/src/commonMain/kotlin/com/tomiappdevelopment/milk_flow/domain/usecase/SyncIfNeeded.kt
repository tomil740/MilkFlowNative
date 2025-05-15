package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.domain.models.ProductMetadata
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.datetime.LocalDate

class SyncIfNeededUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(currentDate: LocalDate): Result<Boolean, DataError> {
        val localMetadata = try {
            repository.getLocalMetadata()
        } catch (e: Exception) {
            return Result.Error(DataError.Local.DISK_FULL) // general DB error fallback
        }

        // 1. Threshold check: already synced today?
        val lastCheckDate = localMetadata.lastSyncCheckDate?.let {
            try {
                LocalDate.parse(it)
            } catch (e: Exception) {
                null // fallback to force sync
            }
        }

        if (lastCheckDate != null &&
            lastCheckDate.year == currentDate.year &&
            lastCheckDate.dayOfYear == currentDate.dayOfYear
        ) {
            return Result.Success(false) // Already synced today
        }

        // 2. Get remote sync timestamp
        val remoteTimestamp = try {
            repository.fetchRemoteSyncTimestamp()
        } catch (e: Exception) {
            // Save failed threshold attempt
            repository.setProductLocalMetaData(
                ProductMetadata(
                    lastProductsUpdate = localMetadata.lastProductsUpdate,
                    lastSyncCheckDate = null
                )
            )
            return Result.Error(DataError.Network.NO_INTERNET)
        }

        // 3. Skip if already up to date
        if (remoteTimestamp == localMetadata.lastProductsUpdate) {
            repository.setProductLocalMetaData(
                ProductMetadata(
                    lastProductsUpdate = localMetadata.lastProductsUpdate,
                    lastSyncCheckDate = currentDate.toString()
                )
            )
            return Result.Success(false)
        }

        // 4. Sync
        val syncResult = repository.syncProductData(
            ProductMetadata(
                lastProductsUpdate = remoteTimestamp,
                lastSyncCheckDate = currentDate.toString()
            )
        )

        if (syncResult is Result.Error) {
            repository.setProductLocalMetaData(ProductMetadata(null, null)) // clear metadata on fail
        }

        return syncResult as Result<Boolean, DataError>
    }
}

