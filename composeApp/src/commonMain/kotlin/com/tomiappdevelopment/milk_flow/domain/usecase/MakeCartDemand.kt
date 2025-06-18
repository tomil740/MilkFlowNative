package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.Demand
import com.tomiappdevelopment.milk_flow.domain.models.User
import com.tomiappdevelopment.milk_flow.domain.repositories.CartRepository
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.DemandError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.domain.util.Result.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result.Success
import com.tomiappdevelopment.milk_flow.domain.util.toDemandError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import network.chaintech.utils.now

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.*

class MakeCartDemand(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(
        authState: User?,
        cartItems: List<CartItem>
    ): Result<Unit, DemandError> = withContext(Dispatchers.IO) {
        try {
            withTimeout(5000) {
                // 1. Validate auth
                val userId = authState?.uid
                    ?: return@withTimeout Error(DemandError.NotAuthenticated)

                if (authState.isDistributer) {
                    return@withTimeout Error(DemandError.DistributerNotAllowed)
                }

                // 2. Validate cart
                if (cartItems.isEmpty()) {
                    return@withTimeout Error(DemandError.EmptyCart)
                }

                // 3. Validate timeframe (8:00 to 18:00)
                val hour = LocalTime.now().hour
                if (hour < 8 || hour >= 18) {
                    return@withTimeout Error(DemandError.InvalidTimeframe)
                }

                // 4. Build and send demand
                val now = LocalDateTime.now()
                val demand = Demand(
                    userId = userId,
                    distributerId = authState.distributerId.orEmpty(),
                    status = Status.pending,
                    createdAt = now,
                    updatedAt = now,
                    products = cartItems,
                    id = ""
                )

                when (val result = cartRepository.makeDemand(demand)) {
                    is Success -> Success(Unit)
                    is Error<DataError.Network> -> Error(result.error.toDemandError())
                }
            }
        } catch (e: TimeoutCancellationException) {
            Error(DemandError.Timeout)
        } catch (e: Exception) {
            Error(DemandError.Unknown)
        }
    }
}


