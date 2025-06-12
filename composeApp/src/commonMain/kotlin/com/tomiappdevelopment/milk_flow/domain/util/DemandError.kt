package com.tomiappdevelopment.milk_flow.domain.util

sealed class DemandError:Error {
    object NotAuthenticated : DemandError()
    object DistributerNotAllowed : DemandError()
    object EmptyCart : DemandError()
    object InvalidTimeframe : DemandError()
    object NoInternet : DemandError()
    object Timeout : DemandError()
    object Unauthorized : DemandError()
    object Conflict : DemandError()
    object RateLimited : DemandError()
    object PayloadTooLarge : DemandError()
    object InvalidResponse : DemandError()
    object ServerError : DemandError()
    object TooManyRequests : DemandError()
    object Serialization : DemandError()
    object Unknown : DemandError()
    object InvalidStatus: DemandError()
}
fun DataError.Network.toDemandError(): DemandError = when (this) {
    DataError.Network.NO_INTERNET -> DemandError.NoInternet
    DataError.Network.UNAUTHORIZED -> DemandError.Unauthorized
    DataError.Network.CONFLICT -> DemandError.Conflict
    DataError.Network.TOO_MANY_REQUESTS -> DemandError.TooManyRequests
    DataError.Network.PAYLOAD_TOO_LARGE -> DemandError.PayloadTooLarge
    DataError.Network.SERVER_ERROR -> DemandError.ServerError
    DataError.Network.SERIALIZATION -> DemandError.Serialization
    DataError.Network.REQUEST_TIMEOUT -> DemandError.Timeout
    DataError.Network.UNKNOWN -> DemandError.Unknown
    DataError.Network.NOT_FOUND -> DemandError.Unknown
}
