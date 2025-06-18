package com.tomiappdevelopment.milk_flow.presentation.CartScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManager
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.core.ConnectionState
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct
import com.tomiappdevelopment.milk_flow.domain.repositories.CartRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.usecase.GetConnectionState
import com.tomiappdevelopment.milk_flow.domain.usecase.MakeCartDemand
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncIfNeededUseCase
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.presentation.util.toUiText
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
import milkflow.composeapp.generated.resources.Res
import milkflow.composeapp.generated.resources.error_not_authenticated_no_data
import milkflow.composeapp.generated.resources.message_cart_requires_auth
import milkflow.composeapp.generated.resources.message_demand_submitted_success
import milkflow.composeapp.generated.resources.message_products_synced_success
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
class CartScreenVm(
    productsRepo: ProductRepository,
    private val cartRepository: CartRepository,
    private val authManager: AuthManager,
    syncIfNeededUseCase:SyncIfNeededUseCase,
    private val makeCartDemand:MakeCartDemand,
    private val getConnectionState: GetConnectionState
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
                getConnectionState.invoke().collectLatest {  connectionState->
                    _uiState.update { it.copy(connectionState = connectionState) }
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

                            uiMessage.send(UiText.StringResource(Res.string.message_products_synced_success))

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
                        uiMessage.send(UiText.StringResource(Res.string.error_not_authenticated_no_data))

                    }
                }
            }
        }
    }



    @OptIn(ExperimentalResourceApi::class)
    fun onEvent(event: CartScreenEvents) {
        when (event) {
            CartScreenEvents.OnMakeDemand -> {
                makeDemandJob?.cancel()
                makeDemandJob = screenModelScope.launch {
                    _uiState.update { it.copy(isLoading = true) }
                    //check live connection observer
                    println("Lie observer: ${_uiState.value.connectionState}")

                    val authState = authManager.userFlow(this).firstOrNull()
                    if (authState == null) {
                        _uiState.update { it.copy(isLoading = false) }
                        uiMessage.send(UiText.StringResource(Res.string.message_cart_requires_auth))
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
                            uiMessage.send((result.error.toUiText()))
                        }

                        is Result.Success -> {
                            cartRepository.clearCart(authState.uid)
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                )
                            }
                            uiMessage.send(UiText.StringResource(Res.string.message_demand_submitted_success))
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
                        uiMessage.send(UiText.StringResource(Res.string.message_cart_requires_auth))
                    }
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

}



















