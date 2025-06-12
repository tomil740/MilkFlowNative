package com.tomiappdevelopment.milk_flow.presentation.core.topBar

import com.tomiappdevelopment.milk_flow.domain.core.ConnectionState

data class TopBarUiState(
    val name: String? = null,
    val isLoggedIn: Boolean = false,
    val isDistributor: Boolean = false,
    val cartItemCount: Int = 0,
    val connectionState:ConnectionState = ConnectionState.Unavailable
)
