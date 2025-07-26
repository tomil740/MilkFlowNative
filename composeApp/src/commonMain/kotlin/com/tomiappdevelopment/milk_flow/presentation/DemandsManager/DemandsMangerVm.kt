package com.tomiappdevelopment.milk_flow.presentation.DemandsManager

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManagerVm
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.SyncStatus
import com.tomiappdevelopment.milk_flow.domain.core.getNextStatus
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import com.tomiappdevelopment.milk_flow.domain.models.ProductMetadata
import com.tomiappdevelopment.milk_flow.domain.models.ProductSummaryItem
import com.tomiappdevelopment.milk_flow.domain.models.UserProductDemand
import com.tomiappdevelopment.milk_flow.domain.models.subModels.UpdateDemandsStatusParams
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.usecase.GetDemandsWithUserNames
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncIfNeededUseCase
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncNewDemands
import com.tomiappdevelopment.milk_flow.domain.usecase.UpdateDemandsStatusUseCase
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.presentation.util.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import milkflow.composeapp.generated.resources.Res
import milkflow.composeapp.generated.resources.error_no_demands
import milkflow.composeapp.generated.resources.error_not_synced
import milkflow.composeapp.generated.resources.error_operation_in_progress
import milkflow.composeapp.generated.resources.error_validation_unknown
import milkflow.composeapp.generated.resources.success_demands_updated
import milkflow.composeapp.generated.resources.success_products_synced
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalResourceApi::class)
class DemandsMangerVm(
    private val productsRepo: ProductRepository,
    syncIfNeededUseCase:SyncIfNeededUseCase,
    private val authManagerVm: AuthManagerVm,
    private val syncNewDemands:SyncNewDemands,
    private val getDemandsWithUserNames: GetDemandsWithUserNames,
    private val updateDemandsStatusUseCase:UpdateDemandsStatusUseCase,
): ScreenModel {


    private var syncNewDemandJob:Job?=null

    private val uiMessage = Channel<UiText>()
    private val _uiState = MutableStateFlow(
        DemandsManagerUiState(
            uiMessage = uiMessage
        )
    )

    private val fullCacheEmptyFlag = MutableStateFlow<Boolean>(false)


    //the observable stateflow ui state that is listening to the original ui state
    var uiState =
        _uiState.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value)

    init {

        combine(
            _uiState.map { it.status }.distinctUntilChanged(),
            _uiState.map { it.isProductView }.distinctUntilChanged(),
            _uiState.map { it.authState }.distinctUntilChanged()
        ) { status, isProductView, authState -> Triple(status, isProductView, authState) }
            .flatMapLatest { (status, isProductView, authState) ->
                if (authState == null) {
                    flowOf(emptyList<DemandWithNames>()).map { demands ->
                        Triple(demands, status, isProductView)
                    }
                } else {
                    getDemandsWithUserNames.invoke(status, authState.uid, authState.isDistributer)
                        .map { demands -> Triple(demands, status, isProductView) }
                }
            }
            .onEach { (demands, status, isProductView) ->
                if (isProductView) {
                    val a = buildProductSummaryItems(demands)
                    _uiState.update { it.copy(productSummaryList = a, demandSummaryList = demands) }
                } else {
                    _uiState.update { it.copy(productSummaryList = emptyList(), demandSummaryList = demands) }
                }
            }
            .launchIn(screenModelScope)



        screenModelScope.launch {

            launch {
                authManagerVm.userState.collect{ authRes ->
                    _uiState.update { it.copy(authState = (authRes)) }
                }
            }

            launch {
                _uiState.update { it.copy(isLoading = true) }
                delay(500)
                if (fullCacheEmptyFlag.value) {
                    //demand a sync
                    delay(500)
                    if (fullCacheEmptyFlag.value) {
                        productsRepo.setProductLocalMetaData(ProductMetadata())
                    }
                }


                val a = syncIfNeededUseCase.invoke()
                when (a) {
                    is Result.Error<DataError> -> {
                        _uiState.update { it.copy(isLoading = false) }
                        uiMessage.send((a.error.toUiText()))
                    }

                    is Result.Success<Boolean> -> {
                        _uiState.update { it.copy(isLoading = false) }
                        if (a.data) {
                            println("A full products replacement ${a.data}")
                            uiMessage.send(UiText.StringResource(Res.string.success_products_synced))
                        }
                    }
                }
                syncNewDemandUseCase()
            }
        }

    }

    fun onEvent(event: DemandsMangerEvents) {
        when (event) {
            is DemandsMangerEvents.OnStatusSelected -> {
                _uiState.update { it.copy(status = event.status) }
            }
            DemandsMangerEvents.OnToggleView -> {
                _uiState.update { it.copy(isProductView = (!it.isProductView)) }
            }
            DemandsMangerEvents.OnUpdateDemandsStatus -> {
                val uiState = _uiState.value

                // Pre-check: validate preconditions
                if (
                    (!uiState.isLoading) &&
                    uiState.demandSummaryList.isNotEmpty() &&
                    uiState.syncStatus == SyncStatus.SUCCESS
                ) {
                    screenModelScope.launch {
                        _uiState.update { it.copy(isLoading = true) }

                        val result = updateDemandsStatusUseCase.invoke(
                            UpdateDemandsStatusParams(
                                uiState.demandSummaryList.map { it.base },
                                targetStatus = uiState.status.getNextStatus()!!
                            ),
                            uiState.authState
                        )

                        when (result) {
                            is Result.Error -> {
                                _uiState.update { it.copy(isLoading = false) }
                                uiMessage.send((result.error.toUiText()))
                            }

                            is Result.Success -> {
                                _uiState.update { it.copy(isLoading = false) }
                                uiMessage.send(UiText.StringResource(Res.string.success_demands_updated))
                                syncNewDemandUseCase()
                            }
                        }
                    }
                } else {
                    // Send feedback if validation fails
                    val errorMsg = when {
                        uiState.isLoading -> UiText.StringResource(Res.string.error_operation_in_progress)
                        uiState.demandSummaryList.isEmpty() -> UiText.StringResource(Res.string.error_no_demands)
                        uiState.syncStatus != SyncStatus.SUCCESS -> UiText.StringResource(Res.string.error_not_synced)
                        else -> UiText.StringResource(Res.string.error_validation_unknown)
                    }
                    screenModelScope.launch {
                        uiMessage.send(errorMsg)
                    }
                }
            }

            DemandsMangerEvents.Refresh -> {
                screenModelScope.launch {
                    syncNewDemandUseCase()
                }
            }
        }
    }

    private fun syncNewDemandUseCase() {
        val auth = _uiState.value.authState ?: return
        syncNewDemandJob?.cancel()
        syncNewDemandJob = screenModelScope.launch {
            _uiState.update { it.copy(syncStatus = SyncStatus.IN_PROGRESS) }
            val result = syncNewDemands.invoke(
                uid = auth.uid,
                isDistributor = auth.isDistributer
            )
            when (result) {
                is Result.Success -> {
                    val hasNewData = result.data
                    _uiState.update {
                        it.copy(
                            syncStatus = SyncStatus.SUCCESS//if a sync ahs been made or not define by hasNewData
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(syncStatus = SyncStatus.ERROR) }
                    // Optionally log: result.exception.message
                }
            }
        }
    }

    suspend fun buildProductSummaryItems(
        demands: List<DemandWithNames>
    ): List<ProductSummaryItem> {
        // Step 1: Flatten to (productId, userName, amount)
        val flattened: List<Triple<Int, String, Int>> = demands.flatMap { demand ->
            demand.products.map { item ->
                Triple(item.productId, demand.userName, item.amount)
            }
        }

        // Step 2: Group by productId AND userName to sum per-user amounts
        val perUserGrouped: Map<Pair<Int, String>, Int> = flattened
            .groupBy { (productId, userName, _) -> productId to userName }
            .mapValues { (_, entries) ->
                entries.sumOf { it.third }
            }

        // Step 3: Re-group by productId to collect user orders
        val summaryMap: Map<Int, Pair<Int, List<UserProductDemand>>> = perUserGrouped
            .entries
            .groupBy(keySelector = { it.key.first }) // productId
            .mapValues { (_, entries) ->
                val usersDemand = entries.map { (key, amountSum) ->
                    UserProductDemand(
                        userName = key.second,
                        amount = amountSum
                    )
                }
                val totalAmount = usersDemand.sumOf { it.amount }
                totalAmount to usersDemand
            }

        // Step 4: Fetch product data from repository
        val productIds = summaryMap.keys.toList()
        val products = productsRepo.getProductsByIds(productIds)

        // Step 5: Map into ProductSummaryItem list
        return products.mapNotNull { product ->
            val (amountSum, usersDemand) = summaryMap[product.id] ?: return@mapNotNull null

            ProductSummaryItem(
                productId = product.id,
                productName = product.name,
                barcode = product.barcode,
                productImgUrl = product.barcode,
                amountSum = amountSum,
                usersDemand = usersDemand
            )
        }
    }






}