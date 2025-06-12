package com.tomiappdevelopment.milk_flow.presentation.DemandsManager

import com.tomiappdevelopment.milk_flow.domain.core.Status

sealed interface DemandsMangerEvents {
    data class OnStatusSelected(val status: Status) : DemandsMangerEvents
    data object OnToggleView : DemandsMangerEvents
    data object OnUpdateDemandsStatus: DemandsMangerEvents
    data object Refresh : DemandsMangerEvents
}