package com.tomiappdevelopment.milk_flow.presentation.CartScreen

import com.tomiappdevelopment.milk_flow.domain.core.ConnectionState
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct

data class CartScreenUiState(
    val cartProducts: List<CartProduct>,
    val authState: String? = null,
    val isLoading: Boolean = false,
    val connectionState:ConnectionState = ConnectionState.Unavailable
)
