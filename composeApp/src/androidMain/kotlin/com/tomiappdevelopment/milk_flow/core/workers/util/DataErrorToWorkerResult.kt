package com.tomiappdevelopment.milk_flow.core.workers.util

import androidx.work.ListenableWorker
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.DemandError

fun DataError.toWorkerResult(): ListenableWorker.Result {
    return when(this) {
        DataError.Local.DISK_FULL -> ListenableWorker.Result.failure()
        DataError.Network.REQUEST_TIMEOUT -> ListenableWorker.Result.retry()
        DataError.Network.UNAUTHORIZED -> ListenableWorker.Result.retry()
        DataError.Network.CONFLICT -> ListenableWorker.Result.retry()
        DataError.Network.TOO_MANY_REQUESTS -> ListenableWorker.Result.retry()
        DataError.Network.NO_INTERNET -> ListenableWorker.Result.retry()
        DataError.Network.PAYLOAD_TOO_LARGE -> ListenableWorker.Result.failure()
        DataError.Network.SERVER_ERROR -> ListenableWorker.Result.retry()
        DataError.Network.SERIALIZATION -> ListenableWorker.Result.failure()
        DataError.Network.UNKNOWN -> ListenableWorker.Result.failure()
        DataError.Network.NOT_FOUND -> ListenableWorker.Result.failure()
    }
}
fun DemandError.toWorkerResult(): ListenableWorker.Result {
    return when (this) {
        DemandError.NoInternet,
        DemandError.Unauthorized,
        DemandError.Conflict,
        DemandError.TooManyRequests,
        DemandError.ServerError,
        DemandError.Timeout -> ListenableWorker.Result.retry()

        DemandError.PayloadTooLarge,
        DemandError.Serialization,
        DemandError.InvalidResponse,
        DemandError.InvalidStatus,
        DemandError.DistributerNotAllowed,
        DemandError.EmptyCart,
        DemandError.InvalidTimeframe -> ListenableWorker.Result.failure()

        DemandError.Unknown,
        DemandError.NotAuthenticated -> ListenableWorker.Result.failure()

        DemandError.RateLimited -> ListenableWorker.Result.failure()
    }
}
