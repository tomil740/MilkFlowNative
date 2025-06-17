package com.tomiappdevelopment.milk_flow.presentation.DemandItem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.core.getNextStatus
import com.tomiappdevelopment.milk_flow.domain.core.getStringName
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.CartSatesAndEvents
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.components.CartHeader
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.components.CartPreviewItem
import com.tomiappdevelopment.milk_flow.presentation.core.components.CheckoutButton
import com.tomiappdevelopment.milk_flow.presentation.DemandItem.components.DemandInfo
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.DemandsMangerScreenClass
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components.DemandPreviewItem
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components.StatusMenuBar
import com.tomiappdevelopment.milk_flow.presentation.core.components.EmptyDataMessage
import com.tomiappdevelopment.milk_flow.presentation.core.components.LoadingSpinner
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.components.ProductDialog
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.datetime.LocalDateTime
import milkflow.composeapp.generated.resources.Res
import milkflow.composeapp.generated.resources.dialog_demand_update_success_message
import milkflow.composeapp.generated.resources.dialog_demand_update_success_title
import milkflow.composeapp.generated.resources.dialog_ok_button
import milkflow.composeapp.generated.resources.error_not_authenticated_no_data
import milkflow.composeapp.generated.resources.label_update_status
import network.chaintech.utils.now
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun DemandItemScreen(demandsItemSatesAndEvents: DemandsItemSatesAndEvents
) {
    val uiState = demandsItemSatesAndEvents.uiState

    val snackBarHostState = remember { SnackbarHostState() }

    val navigator = LocalNavigator.currentOrThrow

    Box {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            },
            bottomBar = {
                if (demandsItemSatesAndEvents.uiState.authState?.isDistributer == true) {

                    CheckoutButton(
                        loading = false,
                        onClick = { demandsItemSatesAndEvents.onUpdateDemandStatus() },
                        label = UiText.StringResource(
                            Res.string.label_update_status,
                            demandsItemSatesAndEvents.uiState.demandItem.status.getNextStatus()
                                ?.getStringName() ?: ""
                        ).asString(),
                        enabled = (demandsItemSatesAndEvents.uiState.demandItem.status != Status.completed &&
                                demandsItemSatesAndEvents.uiState.authState != null)
                    )
                }
            }

        ) {

            LaunchedEffect(demandsItemSatesAndEvents.uiState.uiMessage) {
                demandsItemSatesAndEvents.uiState.uiMessage.consumeAsFlow()
                    .collect {
                        snackBarHostState.showSnackbar(
                            it.asString2(),
                            duration = SnackbarDuration.Short
                        )
                    }
            }


            Column {
                DemandInfo(
                    demand = uiState.demandItem
                )

                AnimatedVisibility(uiState.demandItem.userName.isEmpty()) {
                    val mes = if (uiState.authState == null) {
                       UiText.StringResource(Res.string.error_not_authenticated_no_data).asString()
                    } else {
                        ""
                    }
                    EmptyDataMessage(message = mes)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {


                    item {
                        LoadingSpinner(isLoading = uiState.isLoading)
                    }

                    items(uiState.demandProducts) { item ->
                        CartPreviewItem(
                            cartProduct = item,
                            onEdit = {},
                            isDemandItem = true
                        )
                    }
                }
            }
        }
        if (uiState.showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    TextButton(onClick = {
                        navigator.replaceAll(DemandsMangerScreenClass())
                    }) {
                        Text(UiText.StringResource(Res.string.dialog_ok_button).asString())
                    }
                },
                title = { Text(UiText.StringResource(Res.string.dialog_demand_update_success_title).asString()) },
                text = { Text(UiText.StringResource(Res.string.dialog_demand_update_success_message).asString()) }
            )
        }
    }

}