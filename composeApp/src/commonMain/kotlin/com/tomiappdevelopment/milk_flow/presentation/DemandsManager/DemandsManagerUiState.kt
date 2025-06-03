package com.tomiappdevelopment.milk_flow.presentation.DemandsManager

import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.core.SyncStatus
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

data class DemandsManagerUiState(
    val demandSummaryList: List<DemandWithNames> = emptyList(),
   // val productSummaryList: List<ProductSummaryUiModel> = emptyList(),
    val status: Status = Status.pending,
    val isProductView: Boolean = false,
    val isLoading: Boolean = false,                           // Loading state
    val uiMessage : Channel<UiText>,
    val syncStatus: SyncStatus = SyncStatus.IDLE,
    val authState: String? = null,
)
