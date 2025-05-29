package com.tomiappdevelopment.milk_flow.presentation.CartScreen

import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.Category

sealed interface CartScreenEvents {

    data class UpdateItem(val cartItem: CartItem) : CartScreenEvents
    object OnMakeDemand : CartScreenEvents


}