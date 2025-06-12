package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.domain.core.ConnectionState
import com.tomiappdevelopment.milk_flow.domain.repositories.ConnectionObserver
import kotlinx.coroutines.flow.Flow

class GetConnectionState(
    private val connectionObserver: ConnectionObserver
) {
    suspend operator fun invoke(): Flow<ConnectionState> {
        return connectionObserver.connectionState
    }
}