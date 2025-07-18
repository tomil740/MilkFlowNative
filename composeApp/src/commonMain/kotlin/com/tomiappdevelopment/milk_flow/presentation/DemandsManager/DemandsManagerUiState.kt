package com.tomiappdevelopment.milk_flow.presentation.DemandsManager

import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.core.SyncStatus
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import com.tomiappdevelopment.milk_flow.domain.models.ProductSummaryItem
import com.tomiappdevelopment.milk_flow.domain.models.User
import kotlinx.coroutines.channels.Channel

data class DemandsManagerUiState(
    val demandSummaryList: List<DemandWithNames> = emptyList(),
    val productSummaryList: List<ProductSummaryItem> = emptyList(),
    val status: Status = Status.pending,
    val isProductView: Boolean = false,
    val isLoading: Boolean = false,                           // Loading state
    val uiMessage : Channel<UiText>,
    val syncStatus: SyncStatus = SyncStatus.INIT,
    val authState: User? = null,
)
