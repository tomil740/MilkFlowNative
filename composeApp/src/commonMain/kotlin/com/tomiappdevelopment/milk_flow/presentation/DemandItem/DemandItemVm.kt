package com.tomiappdevelopment.milk_flow.presentation.DemandItem

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManager
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.core.SyncStatus
import com.tomiappdevelopment.milk_flow.domain.core.getNextStatus
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import com.tomiappdevelopment.milk_flow.domain.models.ProductMetadata
import com.tomiappdevelopment.milk_flow.domain.models.ProductSummaryItem
import com.tomiappdevelopment.milk_flow.domain.models.User
import com.tomiappdevelopment.milk_flow.domain.models.UserProductDemand
import com.tomiappdevelopment.milk_flow.domain.models.subModels.UpdateDemandsStatusParams
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.usecase.GetDemandsWithUserNames
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncIfNeededUseCase
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncNewDemands
import com.tomiappdevelopment.milk_flow.domain.usecase.UpdateDemandsStatusUseCase
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.DemandsManagerUiState
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.DemandsMangerEvents
import com.tomiappdevelopment.milk_flow.presentation.util.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.map

@OptIn(ExperimentalCoroutinesApi::class)
class DemandItemVm(
    private val productsRepo: ProductRepository,
    private val demandsRepository: DemandsRepository,
    private val authRepository: AuthRepository,
    private val authManager: AuthManager,
    private val updateDemandsStatusUseCase:UpdateDemandsStatusUseCase
): ScreenModel {

    private val uiMessage = Channel<UiText>()
    private val _uiState = MutableStateFlow(
        DemandItemUiState(
            uiMessage = uiMessage
        )
    )

    //the observable stateflow ui state that is listening to the original ui state
    var uiState =
        _uiState.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value)


    init {
        screenModelScope.launch {
            authManager.userFlow(this).collect{ authRes ->
                _uiState.update { it.copy(authState = (authRes)) }
            }
        }

    }

    fun onUpdateDemandsStatus(){
        val uiState = _uiState.value

        // Pre-check: validate preconditions
        if (!uiState.isLoading &&
            uiState.demandItem.id.isNotEmpty() &&
            uiState.demandItem.status != Status.completed
        ) {
            screenModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                val result = updateDemandsStatusUseCase.invoke(
                    UpdateDemandsStatusParams(
                        listOf(uiState.demandItem.base),
                        targetStatus = uiState.demandItem.status.getNextStatus()!!
                    ),
                    uiState.authState
                )

                when (result) {
                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        uiMessage.send((result.error.toUiText()))
                    }

                    is Result.Success -> {
                        _uiState.update { it.copy(showSuccessDialog = true,isLoading = false) }
                    }
                }
            }
        } else {
            // Send feedback if validation fails
            val errorMsg = when {
                uiState.isLoading -> "Please wait, operation in progress"
                uiState.demandItem.id.isEmpty() -> "No demands to update"
                uiState.demandItem.status == Status.completed -> "Cant update status completed!"
                else -> "Unknown validation error"
            }
            screenModelScope.launch {
                uiMessage.send(UiText.DynamicString(errorMsg))
            }
        }
    }

    fun initWithDemandId(demandId: String) {
        if (_uiState.value.demandItem.id == demandId) return

        screenModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val demand = demandsRepository.getDemandById(demandId)!!

                val matchedProducts = productsRepo.getProductsByIds(demand.products.map { it.productId })

                val theProducts = demand.products.mapIndexed { theIndex,dataObj->
                    CartProduct(
                        if(matchedProducts[theIndex].id == dataObj.productId){matchedProducts[theIndex]}else{matchedProducts.findLast { it.id == dataObj.productId }!!},
                        amount = dataObj.amount
                    )
                }
                val uid = authRepository.getUserObjById( demand.userId)?.name
                val disId = authRepository.getUserObjById( demand.distributerId)?.name

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        demandItem = DemandWithNames(
                            base = demand,
                            userName =uid ?: "",
                            distributerName = disId
                        ),
                        demandProducts = theProducts,
                    )
                }
            } catch (e: Exception) {
                uiMessage.send(UiText.DynamicString("שגיאה בטעינת הביקוש: ${e.message}"))
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


}