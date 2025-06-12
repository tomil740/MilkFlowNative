package com.tomiappdevelopment.milk_flow.domain.repositories

import com.tomiappdevelopment.milk_flow.domain.core.ConnectionState
import kotlinx.coroutines.flow.Flow

interface ConnectionObserver {
    val connectionState: Flow<ConnectionState>
}