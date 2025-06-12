package com.tomiappdevelopment.milk_flow.presentation.CartScreen

import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.ConnectionState
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct
import kotlinx.coroutines.channels.Channel

data class CartScreenUiState(
    val cartProducts: List<CartProduct>,
    val authState: String? = null,
    val isLoading: Boolean = false,
    val uiMessage : Channel<UiText>,
    val connectionState:ConnectionState = ConnectionState.Unavailable
)
