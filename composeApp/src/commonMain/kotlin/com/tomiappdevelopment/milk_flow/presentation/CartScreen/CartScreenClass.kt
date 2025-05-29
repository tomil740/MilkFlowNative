package com.tomiappdevelopment.milk_flow.presentation.CartScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.tomiappdevelopment.milk_flow.domain.models.CartItem

class CartScreenClass():Screen {
    @Composable
    override fun Content() {
        val a = getScreenModel<CartScreenVm>()
        val state by a.uiState.collectAsState()
        val b = CartSatesAndEvents(state,
            onMakeDemand = { a.onEvent(CartScreenEvents.OnMakeDemand)},
            updateItem = {a.onEvent(CartScreenEvents.UpdateItem(it))}
        )

        CartScreen(
            b
        )

    }

}

data class CartSatesAndEvents(
    val uiState: CartScreenUiState,
    val onMakeDemand: ()->Unit,
    val updateItem:(CartItem)-> Unit

)
