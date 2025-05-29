package com.tomiappdevelopment.milk_flow.presentation.CartScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManager
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.data.remote.DemandsRemoteDao
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.DemandDto
import com.tomiappdevelopment.milk_flow.data.util.localDateTimeToMillis
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.repositories.CartRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncIfNeededUseCase
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.ProductCatalogEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import network.chaintech.utils.now

class CartScreenVm(
    productsRepo: ProductRepository,
    private val cartRepository: CartRepository,
    private val authManager: AuthManager,
    syncIfNeededUseCase:SyncIfNeededUseCase,
    private val demandsRemoteDao: DemandsRemoteDao

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


    init {
        screenModelScope.launch {

            launch {
                authManager.authState.collectLatest { authRes ->
                    println("dose it updates from other featuere scope #$authRes")
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
                screenModelScope.launch {
                    withContext(Dispatchers.IO) {
                    _uiState.update { it.copy(isLoading = true) }
                        authManager.userFlow(this).collectLatest { autState ->
                            if (autState != null) {
                                 val a = demandsRemoteDao.makeDemand(
                                    DemandDto(
                                        userId = autState.uid,
                                        distributerId =autState.distributerId,
                                        status = Status.completed,
                                        products = _uiState.value.cartProducts.map {
                                            CartItem(productId = it.product.id, amount = it.amount)
                                        }
                                    )
                                )

                                withContext(Dispatchers.Main) {
                                    when (a) {
                                        is Result.Error<DataError> -> {
                                            uiMessage.send(UiText.DynamicString("a demand was fail ${a.error}"))
                                        }

                                        is Result.Success<Unit> -> {
                                            uiMessage.send(UiText.DynamicString("a demand was made"))
                                        }
                                    }
                                }
                                        }

                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        cartProducts = listOf()
                                    )
                                }
                             //   uiMessage.send(UiText.DynamicString("a demand was made"))
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



















