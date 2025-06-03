package com.tomiappdevelopment.milk_flow.presentation.DemandsManager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.CartSatesAndEvents
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.components.CartHeader
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.components.CartPreviewItem
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.components.CheckoutButton
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components.DemandPreviewItem
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components.StatusMenuBar
import com.tomiappdevelopment.milk_flow.presentation.core.components.LoadingSpinner
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.components.ProductDialog
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
fun DemandsMangerScreen(demandsMangerSatesAndEvents: DemandsMangerSatesAndEvents
) {
    val uiState = demandsMangerSatesAndEvents.uiState

    val snackBarHostState = remember { SnackbarHostState() }

    Box {

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            },
            bottomBar = {
                CheckoutButton(
                    loading = false,
                    onClick = {  },
                )
            }

        ) {

            LaunchedEffect(demandsMangerSatesAndEvents.uiState.uiMessage) {
                demandsMangerSatesAndEvents.uiState.uiMessage.consumeAsFlow()
                    .collect {
                        snackBarHostState.showSnackbar(
                            it.asString2(),
                            duration = SnackbarDuration.Long
                        )
                    }
            }


            Column {
                //header...

                StatusMenuBar(currentStatus=uiState.status, onStatusChange = demandsMangerSatesAndEvents.onStatusSelected,
                    syncStatus=uiState.syncStatus )


                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {


                    item {
                        LoadingSpinner(isLoading = uiState.isLoading)
                    }

                    items(uiState.demandSummaryList) { item ->
                        DemandPreviewItem(demand = item,false)

                    }
                }
            }
        }
    }
}