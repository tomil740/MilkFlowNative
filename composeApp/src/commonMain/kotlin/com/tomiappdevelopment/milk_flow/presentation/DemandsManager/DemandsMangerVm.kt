package com.tomiappdevelopment.milk_flow.presentation.DemandsManager

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManager
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.SyncStatus
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import com.tomiappdevelopment.milk_flow.domain.models.ProductMetadata
import com.tomiappdevelopment.milk_flow.domain.models.ProductSummaryItem
import com.tomiappdevelopment.milk_flow.domain.models.UserProductDemand
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.usecase.GetDemandsWithUserNames
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncIfNeededUseCase
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncNewDemands
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
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

@OptIn(ExperimentalCoroutinesApi::class)
class DemandsMangerVm(
    private val productsRepo: ProductRepository,
    syncIfNeededUseCase:SyncIfNeededUseCase,
    private val authManager: AuthManager,
    private val syncNewDemands:SyncNewDemands,
    private val getDemandsWithUserNames: GetDemandsWithUserNames
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
                    // update UI for product view here if needed
                    val a = buildProductSummaryItems(demands)
                    _uiState.update { it.copy(productSummaryList = a) }
                } else {
                    _uiState.update { it.copy(demandSummaryList = demands) }
                }
            }
            .launchIn(screenModelScope)



        screenModelScope.launch {


            launch {
                authManager.userFlow(this).collect{ authRes ->
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
                    is Result.Error<Error> -> {
                        _uiState.update { it.copy(isLoading = false) }
                        uiMessage.send(UiText.DynamicString(a.error.toString()))
                    }

                    is Result.Success<Boolean> -> {
                        _uiState.update { it.copy(isLoading = false) }
                        if (a.data) {
                            println("A full products replacement ${a.data}")
                            uiMessage.send(UiText.DynamicString("Successfully synced"))
                        }
                    }
                }
                syncNewDemandUseCase()
            }
        }

    }

    fun onEvent(event: DemandsMangerEvents) {
        when (event) {
            is DemandsMangerEvents.OnDemandItemClick -> TODO()
            is DemandsMangerEvents.OnStatusSelected -> {
                _uiState.update { it.copy(status = event.status) }
            }
            DemandsMangerEvents.OnToggleView -> {
                _uiState.update { it.copy(isProductView = (!it.isProductView)) }
            }
            DemandsMangerEvents.OnUpdateDemandsStatus -> TODO()
            DemandsMangerEvents.Refresh -> TODO()
        }
    }

    fun syncNewDemandUseCase() {
        val auth = _uiState.value.authState ?: return  // prevent crash
        syncNewDemandJob?.cancel()
        syncNewDemandJob = screenModelScope.launch {
            _uiState.update { it.copy(syncStatus = SyncStatus.IN_PROGRESS) }
            try {
               syncNewDemands.invoke(
                   uid = auth.uid,
                   isDistributor = auth.isDistributer
                )
                _uiState.update { it.copy(syncStatus = SyncStatus.SUCCESS) }
            } catch (e: Exception) {
                _uiState.update { it.copy(syncStatus = SyncStatus.ERROR) }
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