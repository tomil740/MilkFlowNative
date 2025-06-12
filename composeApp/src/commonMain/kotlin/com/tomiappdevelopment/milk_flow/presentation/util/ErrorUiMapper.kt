package com.tomiappdevelopment.milk_flow.presentation.util

import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.DemandError
import milkflow.composeapp.generated.resources.Res
import milkflow.composeapp.generated.resources.error_conflict
import milkflow.composeapp.generated.resources.error_disk_full
import milkflow.composeapp.generated.resources.error_distributer_not_allowed
import milkflow.composeapp.generated.resources.error_empty_cart
import milkflow.composeapp.generated.resources.error_invalid_response
import milkflow.composeapp.generated.resources.error_invalid_status
import milkflow.composeapp.generated.resources.error_invalid_timeframe
import milkflow.composeapp.generated.resources.error_no_internet
import milkflow.composeapp.generated.resources.error_not_authenticated
import milkflow.composeapp.generated.resources.error_not_found
import milkflow.composeapp.generated.resources.error_payload_too_large
import milkflow.composeapp.generated.resources.error_rate_limited
import milkflow.composeapp.generated.resources.error_serialization
import milkflow.composeapp.generated.resources.error_server_error
import milkflow.composeapp.generated.resources.error_timeout
import milkflow.composeapp.generated.resources.error_too_many_requests
import milkflow.composeapp.generated.resources.error_unauthorized
import milkflow.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource

@OptIn(ExperimentalResourceApi::class)
fun DemandError.toUiText(): UiText = when (this) {
    is DemandError.NotAuthenticated -> UiText.StringResource(Res.string.error_not_authenticated)
    is DemandError.DistributerNotAllowed -> UiText.StringResource(Res.string.error_distributer_not_allowed)
    is DemandError.EmptyCart -> UiText.StringResource(Res.string.error_empty_cart)
    is DemandError.InvalidTimeframe -> UiText.StringResource(Res.string.error_invalid_timeframe)
    is DemandError.NoInternet -> UiText.StringResource(Res.string.error_no_internet)
    is DemandError.Timeout -> UiText.StringResource(Res.string.error_timeout)
    is DemandError.Unauthorized -> UiText.StringResource(Res.string.error_unauthorized)
    is DemandError.Conflict -> UiText.StringResource(Res.string.error_conflict)
    is DemandError.RateLimited -> UiText.StringResource(Res.string.error_rate_limited)
    is DemandError.PayloadTooLarge -> UiText.StringResource(Res.string.error_payload_too_large)
    is DemandError.InvalidResponse -> UiText.StringResource(Res.string.error_invalid_response)
    is DemandError.ServerError -> UiText.StringResource(Res.string.error_server_error)
    is DemandError.TooManyRequests -> UiText.StringResource(Res.string.error_too_many_requests)
    is DemandError.Serialization -> UiText.StringResource(Res.string.error_serialization)
    is DemandError.InvalidStatus -> UiText.StringResource(Res.string.error_invalid_status)
    is DemandError.Unknown -> UiText.StringResource(Res.string.error_unknown)

    else -> UiText.DynamicString("Unknown error: ${this::class.simpleName}")
}

@OptIn(ExperimentalResourceApi::class)
fun DataError.toUiText(): UiText = when (this) {
    DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(Res.string.error_timeout)
    DataError.Network.UNAUTHORIZED -> UiText.StringResource(Res.string.error_unauthorized)
    DataError.Network.CONFLICT -> UiText.StringResource(Res.string.error_conflict)
    DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(Res.string.error_too_many_requests)
    DataError.Network.NO_INTERNET -> UiText.StringResource(Res.string.error_no_internet)
    DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(Res.string.error_payload_too_large)
    DataError.Network.SERVER_ERROR -> UiText.StringResource(Res.string.error_server_error)
    DataError.Network.SERIALIZATION -> UiText.StringResource(Res.string.error_serialization)
    DataError.Network.UNKNOWN -> UiText.StringResource(Res.string.error_unknown)
    DataError.Network.NOT_FOUND -> UiText.StringResource(Res.string.error_not_found)

    DataError.Local.DISK_FULL -> UiText.StringResource(Res.string.error_disk_full)
}
