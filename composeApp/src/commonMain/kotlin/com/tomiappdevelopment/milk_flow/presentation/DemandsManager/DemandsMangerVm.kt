package com.tomiappdevelopment.milk_flow.presentation.DemandsManager

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManager
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.SyncStatus
import com.tomiappdevelopment.milk_flow.domain.models.Demand
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import com.tomiappdevelopment.milk_flow.domain.models.ProductMetadata
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DemandsMangerVm(
    productsRepo: ProductRepository,
    syncIfNeededUseCase:SyncIfNeededUseCase,
    private val authManager: AuthManager,
    private val demandsRepo: DemandsRepository,
    private val syncNewDemands:SyncNewDemands
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
            _uiState.map { it.isProductView }.distinctUntilChanged()
        ) { status, isProductView -> status to isProductView }
            .flatMapLatest { (status, isProductView) ->
                demandsRepo.getDemands(status).map { demands ->
                    Triple(demands, status, isProductView)
                }
            }
            .onEach { (demands, status, isProductView) ->
                if (isProductView) {
                    //  val productSummary = summarizeByProduct(demands)
                    //  _uiState.update { it.copy(productSummaryList = productSummary) }
                } else {
                    val demandSummary = demands.map {
                        DemandWithNames(base = it, userName = "name", distributerName = "name")
                    }
                    _uiState.update { it.copy(demandSummaryList = demandSummary) }
                }
            }
            .launchIn(screenModelScope)


        screenModelScope.launch {


            launch {
                authManager.authState.collect { authRes ->
                    _uiState.update { it.copy(authState = (authRes?.localId)) }
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
            DemandsMangerEvents.OnToggleView -> TODO()
            DemandsMangerEvents.OnUpdateDemandsStatus -> TODO()
            DemandsMangerEvents.Refresh -> TODO()
        }
    }

    fun syncNewDemandUseCase() {
        syncNewDemandJob?.cancel()
        syncNewDemandJob = screenModelScope.launch {
            _uiState.update { it.copy(syncStatus = SyncStatus.IN_PROGRESS) }
            try {
                syncNewDemands.invoke()
                _uiState.update { it.copy(syncStatus = SyncStatus.SUCCESS) }
            } catch (e: Exception) {
                _uiState.update { it.copy(syncStatus = SyncStatus.ERROR) }
            }
        }
    }



}