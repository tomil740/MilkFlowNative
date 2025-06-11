package com.tomiappdevelopment.milk_flow.presentation.DemandItem

import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct
import com.tomiappdevelopment.milk_flow.domain.models.Demand
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import com.tomiappdevelopment.milk_flow.domain.models.User
import kotlinx.coroutines.channels.Channel
import kotlinx.datetime.LocalDateTime
import network.chaintech.utils.now

data class DemandItemUiState(
    val demandItem: DemandWithNames= DemandWithNames(base = Demand("","","", Status.pending,
        LocalDateTime.now(), LocalDateTime.now(),listOf()), userName = "", distributerName = ""
    ),
    val demandProducts: List<CartProduct> = listOf(),
    val authState: User? = null,
    val isLoading: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val uiMessage : Channel<UiText>,
)
