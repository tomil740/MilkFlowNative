package com.tomiappdevelopment.milk_flow.presentation.DemandsManager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.tomiappdevelopment.milk_flow.domain.core.Status

class DemandsMangerScreenClass():Screen {
    @Composable
    override fun Content() {
        val a = getScreenModel<DemandsMangerVm>()
        val state by a.uiState.collectAsState()
        val b = DemandsMangerSatesAndEvents(
            uiState = state,
            onStatusSelected = { status -> a.onEvent(DemandsMangerEvents.OnStatusSelected(status)) },
            onToggleView = { a.onEvent(DemandsMangerEvents.OnToggleView) },
            onUpdateDemandsStatus = { a.onEvent(DemandsMangerEvents.OnUpdateDemandsStatus) },
            refresh = { a.onEvent(DemandsMangerEvents.Refresh) }
        )

        DemandsMangerScreen(
            b
        )

    }

}

data class DemandsMangerSatesAndEvents(
    val uiState: DemandsManagerUiState,
    val onStatusSelected: (Status) -> Unit,
    val onToggleView: () -> Unit,
    val onUpdateDemandsStatus: () -> Unit,
    val refresh: () -> Unit
)