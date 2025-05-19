package com.tomiappdevelopment.milk_flow.domain.util

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

fun mapHttpResponse(e: Exception): DataError.Network {
    return when (e) {
        is SerializationException -> DataError.Network.SERIALIZATION
        is IOException -> DataError.Network.NO_INTERNET
        is ResponseException -> {
            when (e.response.status.value) {
                400 -> DataError.Network.UNAUTHORIZED
                401 -> DataError.Network.UNAUTHORIZED
                409 -> DataError.Network.CONFLICT
                413 -> DataError.Network.PAYLOAD_TOO_LARGE
                429 -> DataError.Network.TOO_MANY_REQUESTS
                in 500..599 -> DataError.Network.SERVER_ERROR
                else -> DataError.Network.UNKNOWN
            }
        }
        else -> DataError.Network.UNKNOWN
    }
}
