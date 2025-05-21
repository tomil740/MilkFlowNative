package com.tomiappdevelopment.milk_flow.presentation.core.topBar

data class TopBarUiState(
    val name: String? = null,
    val isLoggedIn: Boolean = false,
    val isDistributor: Boolean = false,
    val cartItemCount: Int = 0
)
