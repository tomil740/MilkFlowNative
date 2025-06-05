package com.tomiappdevelopment.milk_flow.presentation.CartScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManager
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct
import com.tomiappdevelopment.milk_flow.domain.repositories.CartRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.usecase.MakeCartDemand
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncIfNeededUseCase
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartScreenVm(
    productsRepo: ProductRepository,
    private val cartRepository: CartRepository,
    private val authManager: AuthManager,
    syncIfNeededUseCase:SyncIfNeededUseCase,
    private val makeCartDemand:MakeCartDemand
    ): ScreenModel {

    private val uiMessage = Channel<UiText>()


    private val _uiState = MutableStateFlow(
        CartScreenUiState(
            cartProducts = listOf(),
            isLoading = false,
            uiMessage = uiMessage
        )
    )
    val uiState = _uiState.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value)

    var makeDemandJob: Job? =null

    init {
        screenModelScope.launch {

            launch {
                authManager.authState.collectLatest { authRes ->
                    _uiState.update { it.copy(authState = authRes?.localId) }
                }
            }
            launch {

                // Run sync once before observing the cart
                val syncResult = syncIfNeededUseCase()

                when (syncResult) {
                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        uiMessage.send(UiText.DynamicString(syncResult.error.toString()))
                    }

                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        if (syncResult.data) {

                            uiMessage.send(UiText.DynamicString("Successfully synced"))
                        }
                    }
                }
            }
            launch {

                // Start listening to the user & cart only after sync
                authManager.authState.collectLatest { autState->
                    if (autState != null) {
                        cartRepository.getCart(_uiState.value.authState!!).collect { cartItems ->
                            val cartProducts = productsRepo.getProductsByIds(
                                cartItems.map { it.productId }
                            )

                            // Safer mapping (index mapping could break if mismatch)
                            val productsMap = cartProducts.associateBy { it.id }

                            _uiState.update {
                                it.copy(
                                    cartProducts = cartItems.mapNotNull { item ->
                                        productsMap[item.productId]?.let { product ->
                                            CartProduct(product, item.amount)
                                        }
                                    }
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(cartProducts = listOf())
                        }
                        uiMessage.send(UiText.DynamicString("Auth to view your cart..."))

                    }
                }
            }
        }
    }



    fun onEvent(event: CartScreenEvents) {
        when (event) {
            CartScreenEvents.OnMakeDemand -> {
                makeDemandJob?.cancel()
                makeDemandJob = screenModelScope.launch {
                    _uiState.update { it.copy(isLoading = true) }
                    delay(500)
                    val authState = authManager.userFlow(this).firstOrNull()
                    if (authState == null) {
                        _uiState.update { it.copy(isLoading = false) }
                        uiMessage.send(UiText.DynamicString("User not authenticated"))
                        return@launch
                    }

                    val result = makeCartDemand(
                        authState = authState,
                        cartItems = _uiState.value.cartProducts.map {
                            CartItem(it.product.id, it.amount)
                        }
                    )

                    when (result) {
                        is Result.Error -> {
                             _uiState.update { it.copy(isLoading = false) }
                            uiMessage.send(UiText.DynamicString("Demand failed: ${result.error}"))
                        }

                        is Result.Success -> {
                            cartRepository.clearCart(authState.uid)
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                )
                            }
                            uiMessage.send(UiText.DynamicString("Demand submitted successfully"))
                        }
                    }
                }
            }


            is CartScreenEvents.UpdateItem -> {
                screenModelScope.launch {
                    _uiState.update { it.copy(isLoading = true) }
                    if (uiState.value.authState != null) {
                        //delete item
                        if (event.cartItem.amount == -1) {
                            cartRepository.removeItemFromCart(
                                uiState.value.authState!!,
                                event.cartItem.productId
                            )
                        }
                        else{
                            cartRepository.updateExistCartItem(
                                uiState.value.authState!!,
                                event.cartItem
                            )
                        }
                    }else{
                        uiMessage.send(UiText.DynamicString("Auth to make cart actions..."))
                    }
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

}



















