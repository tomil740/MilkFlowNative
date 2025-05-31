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

class MakeCartDemand(
    private val cartRepository: CartRepository,
    // private val connectionChecker: ConnectionChecker
) {
    suspend operator fun invoke(
        authState: User?,
        cartItems: List<CartItem>
    ): Result<Unit, DemandError> = withContext(Dispatchers.IO) {
        try {
            // 1. Validate auth
            val userId = authState?.uid
                ?: return@withContext Result.Error(DemandError.NotAuthenticated)

            if (authState.isDistributer) {
                return@withContext Result.Error(DemandError.DistributerNotAllowed)
            }

            // 2. Validate cart
            if (cartItems.isEmpty()) {
                return@withContext Error(DemandError.EmptyCart)
            }

            // 3. Validate timeframe (8:00 to 12:00)
            val hour = LocalTime.now().hour
            if (hour < 8 || hour >= 12) {
                return@withContext Result.Error(DemandError.InvalidTimeframe)
            }

            // 4. Optional: Check connectivity
            // if (!connectionChecker.isConnected()) {
            //     return@withContext Result.Error(DemandError.NoInternet)
            // }

            // 5. Build and send demand
            val now = LocalDateTime.now()
            val demand = Demand(
                userId = userId,
                distributerId = authState.distributerId.orEmpty(),
                status = Status.pending,
                createdAt = now,
                updatedAt = now,
                products = cartItems
            )

            when (val result = cartRepository.makeDemand(demand)) {
                is Success -> Result.Success(Unit)
                is Error<DataError.Network> -> Result.Error(result.error.toDemandError())
            }
        } catch (e: Exception) {
            Result.Error(DemandError.Unknown)
        }
    }
}

