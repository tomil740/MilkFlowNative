package com.tomiappdevelopment.milk_flow.presentation.DemandsManager

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.core.getNextStatus
import com.tomiappdevelopment.milk_flow.domain.core.getStringName
import com.tomiappdevelopment.milk_flow.presentation.core.components.CheckoutButton
import com.tomiappdevelopment.milk_flow.presentation.DemandItem.DemandItemScreenClass
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components.DemandPreviewItem
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components.ProductSummaryItemView
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components.StatusMenuBar
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components.TwoWaySwitch
import com.tomiappdevelopment.milk_flow.presentation.core.components.EmptyDataMessage
import com.tomiappdevelopment.milk_flow.presentation.core.components.LoadingSpinner
import kotlinx.coroutines.flow.consumeAsFlow
import milkflow.composeapp.generated.resources.Res
import milkflow.composeapp.generated.resources.label_update_status
import milkflow.composeapp.generated.resources.msg_user_not_authenticated
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DemandsMangerScreen(demandsMangerSatesAndEvents: DemandsMangerSatesAndEvents
) {
    val uiState = demandsMangerSatesAndEvents.uiState

    val snackBarHostState = remember { SnackbarHostState() }

    val navigator = LocalNavigator.currentOrThrow

    val isDistributer = demandsMangerSatesAndEvents.uiState.authState?.isDistributer == true

    Box {

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            },
            bottomBar = {
                if(isDistributer) {
                    CheckoutButton(
                        loading = false,
                        onClick = { demandsMangerSatesAndEvents.onUpdateDemandsStatus(false) },
                        label = UiText.StringResource(
                            Res.string.label_update_status,
                            demandsMangerSatesAndEvents.uiState.status.getNextStatus()
                                ?.getStringName() ?: ""
                        ).asString(),
                        enabled = (demandsMangerSatesAndEvents.uiState.status != Status.completed &&
                                demandsMangerSatesAndEvents.uiState.authState != null)
                    )
                }
            }
        ) {

            LaunchedEffect(demandsMangerSatesAndEvents.uiMessage) {
                demandsMangerSatesAndEvents.uiMessage
                    .collect {
                        snackBarHostState.showSnackbar(
                            it.asString2(),
                            duration = SnackbarDuration.Short
                        )
                    }
            }


            Column {
                //header...

                StatusMenuBar(
                    currentStatus=uiState.status,
                    onStatusChange = demandsMangerSatesAndEvents.onStatusSelected,
                    syncStatus=uiState.syncStatus,
                    onSyncClicked = demandsMangerSatesAndEvents.refresh)

                TwoWaySwitch(isProductSummary = uiState.isProductView, onToggle = {demandsMangerSatesAndEvents.onToggleView()})

                //its set to on the condition (|| or ) becasue we lazyly calcualte the
                //summary
                AnimatedVisibility(uiState.demandSummaryList.isEmpty()) {
                    val mes = if (uiState.authState == null) {
                        stringResource(Res.string.msg_user_not_authenticated)
                    } else {
                        ""
                    }
                    EmptyDataMessage(message = mes)
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight(if(isDistributer){0.85f}else{1f})
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {


                    item {
                        LoadingSpinner(isLoading = uiState.isLoading)
                    }

                    if (uiState.isProductView) {
                        items(uiState.productSummaryList, key = {it.productId}) { item ->
                            ProductSummaryItemView(item)
                        }
                    } else {
                        items(uiState.demandSummaryList, key = {it.id}) { item ->
                            DemandPreviewItem(
                                demand = item,
                                isDistributer = uiState.authState?.isDistributer ?: false,
                                onClick = {navigator.replaceAll(DemandItemScreenClass(theDemandId = item.id))},
                                onDemandDel = {demandsMangerSatesAndEvents.onUpdateDemandsStatus(true)}
                            )
                        }
                    }
                }
            }
        }
    }
}