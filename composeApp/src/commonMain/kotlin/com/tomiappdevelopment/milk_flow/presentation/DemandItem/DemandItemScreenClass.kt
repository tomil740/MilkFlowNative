package com.tomiappdevelopment.milk_flow.presentation.DemandItem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class DemandItemScreenClass(private val theDemandId: String):Screen {
    @Composable
    override fun Content() {
        val a = getScreenModel<DemandItemVm>()

        LaunchedEffect(theDemandId) {
            a.initWithDemandId(demandId = theDemandId)
        }

        val state by a.uiState.collectAsState()
        val b = DemandsItemSatesAndEvents(
            uiState = state,
            onUpdateDemandStatus = { }//a.onEvent(DemandsMangerEvents.OnUpdateDemandsStatus) },
        )
        DemandItemScreen(
            b
        )

    }

}

data class DemandsItemSatesAndEvents(
    val uiState: DemandItemUiState,
    val onUpdateDemandStatus:()-> Unit
)