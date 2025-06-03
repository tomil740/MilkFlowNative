package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.domain.models.ProductMetadata
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock.System
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/*

SyncIfNeededUseCase will check the algortem and if demand a sync of server data will apply
the return value work as below:
* on process fail will return Error
* on regular work and no sync return false result Success(false)
* on sync will return true result Success(false)

according to that need to implement soem retry mechnisem on error when error apper the process hsould run again
 */

class SyncIfNeededUseCase(
    private val repository: ProductRepository,
    private val demandsRepository: DemandsRepository
) {
    private val maxRetryAttempts = 3
    private val retryDelayMillis = 2000L // 2 seconds

    suspend operator fun invoke(): Result<Boolean, Error> {
        return withContext(Dispatchers.IO) {
            // Retry logic to handle intermittent failures
            retryOperation { syncOperation() }
        }
    }

    private suspend fun retryOperation(operation: suspend () -> Result<Boolean, Error>): Result<Boolean, Error> {
        var attempt = 0
        var result: Result<Boolean, Error> = Result.Error(DataError.Network.SERVER_ERROR) // Default error value

        while (attempt < maxRetryAttempts) {
            result = operation()  // Update result with the outcome of the operation
            if (result is Result.Success) {
                return result // If operation is successful, return the result
            } else {
                attempt++
                if (attempt < maxRetryAttempts) {
                    delay(retryDelayMillis) // Wait before retrying
                }
            }
        }

        // After all retries, return the last result
        return result
    }

    private suspend fun syncOperation(): Result<Boolean, Error> {
        val localMetadata = try {
            repository.getLocalMetadata()
        } catch (e: Exception) {
            return Result.Error(DataError.Local.DISK_FULL) // Fallback error for DB issues
        }

        // 1. Check if sync has already occurred today
        val lastCheckDate = localMetadata.lastSyncCheckDate?.let {
            try {
                LocalDate.parse(it)
            } catch (e: Exception) {
                null // If the date is malformed, force a sync
            }
        }

        val currentDate = System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        if (lastCheckDate != null &&
            lastCheckDate.year == currentDate.year &&
            lastCheckDate.dayOfYear == currentDate.dayOfYear
        ) {
            return Result.Success(false) // Already synced today
        }else{
            val thresholdTime = System.now().toEpochMilliseconds() - 72 * 60 * 60 * 1000L
            demandsRepository.cleanOldDemands(thresholdTime)

        }

        // 2. Fetch remote sync timestamp
        val remoteTimestamp = try {
            repository.fetchRemoteSyncTimestamp()
        } catch (e: Exception) {
            return Result.Error(DataError.Network.NO_INTERNET) // No internet connection
        }

        // Update the local metadata with the new sync check date
        repository.setProductLocalMetaData(
            ProductMetadata(
                lastProductsUpdate = remoteTimestamp,
                lastSyncCheckDate = currentDate.toString()
            )
        )

        // 3. Check if data is up-to-date
        if (remoteTimestamp == localMetadata.lastProductsUpdate) {
            return Result.Success(false) // No need to sync
        }

        // 4. Perform the data sync
        val syncResult = repository.syncProductData(
            ProductMetadata(
                lastProductsUpdate = remoteTimestamp,
                lastSyncCheckDate = currentDate.toString()
            )
        )

        return when (syncResult) {
            is Result.Error -> Result.Error(DataError.Network.SERVER_ERROR) // If sync fails
            is Result.Success -> Result.Success(syncResult.data) // Return sync success
        }
    }
}


