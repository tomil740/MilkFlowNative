package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.domain.models.ProductMetadata
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import network.chaintech.utils.now


/*

SyncIfNeededUseCase will check the algortem and if demand a sync of server data will apply
the return value work as below:
* on process fail will return Error
* on regular work and no sync return false result Success(false)
* on sync will return true result Success(false)

according to that need to implement soem retry mechnisem on error when error apper the process hsould run again
 */

class SyncIfNeededUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): Result<Boolean, Error> {

        val localMetadata = try {
            repository.getLocalMetadata()
        } catch (e: Exception) {
            return Result.Error(DataError.Local.DISK_FULL) // general DB error fallback
        }
        println("@@@@@@ Check @@@@@@@ ${localMetadata}")

        // 1. Threshold check: already synced today?
        val lastCheckDate = localMetadata.lastSyncCheckDate?.let {
            try {
                LocalDate.parse(it)
            } catch (e: Exception) {
                null // fallback to force sync
            }
        }
        println("@@@@@@ Check @@@@@@@ ${localMetadata.lastSyncCheckDate}")

        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        if (lastCheckDate != null&&
            lastCheckDate.year == currentDate.year &&
            lastCheckDate.dayOfYear == currentDate.dayOfYear
        ) {
            return Result.Success(false) // Already synced today
        }

        // 2. Get remote sync timestamp
        val remoteTimestamp = try {
            repository.fetchRemoteSyncTimestamp()
        } catch (e: Exception) {
            return Result.Error(DataError.Network.NO_INTERNET)
        }
        //update the check
        repository.setProductLocalMetaData(
            ProductMetadata(
                lastProductsUpdate = remoteTimestamp,
                lastSyncCheckDate = currentDate.toString()
            )
        )

        // 3. Skip if already up to date
        if (remoteTimestamp == localMetadata.lastProductsUpdate) {

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
          //  repository.setProductLocalMetaData(ProductMetadata(null, null)) // clear metadata on fail
            return Result.Error(DataError.Network.SERVER_ERROR)
        }
       val a = when(syncResult){
            is Result.Error<Error> -> Result.Error(syncResult.error)
            is Result.Success<Boolean> -> Result.Success(syncResult.data)
        }

        return a

    }
}

