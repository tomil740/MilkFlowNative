package com.tomiappdevelopment.milk_flow.presentation.DemandsManager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.Status
import kotlinx.coroutines.flow.Flow

class DemandsMangerScreenClass():Screen {
    @Composable
    override fun Content() {
        val a = getScreenModel<DemandsMangerVm>()
        val state by a.uiState.collectAsState()
        val b = DemandsMangerSatesAndEvents(
            uiState = state,
            onStatusSelected = { status -> a.onEvent(DemandsMangerEvents.OnStatusSelected(status)) },
            onToggleView = { a.onEvent(DemandsMangerEvents.OnToggleView) },
            onUpdateDemandsStatus = { isToDel1 -> a.onEvent(DemandsMangerEvents.OnUpdateDemandsStatus(isToDel =isToDel1 )) },
            uiMessage = a.uiMessage,
            refresh = { a.onEvent(DemandsMangerEvents.Refresh) }
        )

        DemandsMangerScreen(
            b
        )

    }

}

data class DemandsMangerSatesAndEvents(
    val uiState: DemandsManagerUiState,
    val uiMessage: Flow<UiText>,
    val onStatusSelected: (Status) -> Unit,
    val onToggleView: () -> Unit,
    val onUpdateDemandsStatus: (Boolean) -> Unit,
    val refresh: () -> Unit
)