package com.tomiappdevelopment.milk_flow.presentation.productCatalog

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManager
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.models.Category
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.models.ProductMetadata
import com.tomiappdevelopment.milk_flow.domain.repositories.CartRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.usecase.GetAuthorizedProducts
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncIfNeededUseCase
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


class ProductCatalogVm(
    productsRepo: ProductRepository,
    syncIfNeededUseCase:SyncIfNeededUseCase,
    getAuthorizedProducts:GetAuthorizedProducts,
    private val authManager: AuthManager,
    private val cartRepository: CartRepository
    ): ScreenModel{

    private val uiMessage = Channel<UiText>()


    private val _uiState = MutableStateFlow(
        ProductCatalogUiState(
            uiMessage = uiMessage
        )
    )

    private val fullCacheEmptyFlag = MutableStateFlow<Boolean>(false)

    //the observable stateflow ui state that is listening to the original ui state
    var uiState = _uiState.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value)

    init {
        screenModelScope.launch {

            launch {
                authManager.authState.collect { authRes->
                    _uiState.update { it.copy(authState=(authRes?.localId)) }
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
            }



            launch {
                getAuthorizedProducts.invoke(this).collect { products ->
                    var theProducts = products
                    //flag of problem in all cached data
                    if (products.size == 1 && products.first().id == -1) {
                        fullCacheEmptyFlag.value = true
                        theProducts = emptyList()
                    } else {
                        fullCacheEmptyFlag.value = false
                    }

                    _uiState.update { it.copy(products = theProducts) }
                    filterByCategory(uiState.value.selectedCategory)
                    if (uiState.value.filteredProducts.isEmpty()) {
                        onEvent(ProductCatalogEvents.OnEmptyProducts)
                    }
                }
            }
        }

    }

    fun onEvent(event:ProductCatalogEvents) {
        when(event){
            is ProductCatalogEvents.AddToCart -> {
                screenModelScope.launch {
                    _uiState.update { it.copy(isLoading =true) }
                    var theMes = "item has been Add to cart"
                    if (_uiState.value.authState!=null){
                        cartRepository.addItemToCart(
                            uid = _uiState.value.authState!!,
                            item = event.cartItem
                        )
                    }else{
                        theMes =  "Auth to get access to cart"
                    }

                    _uiState.update { it.copy(isLoading = false, emptyDataMes =theMes) }

                }
            }
            is ProductCatalogEvents.OnCategorySelected -> {
                filterByCategory(event.category)
            }
            ProductCatalogEvents.Refresh -> TODO()

            ProductCatalogEvents.OnEmptyProducts -> {

                //get the matched case(connection error, auth or a server error) and update the matched field
                var theMes = "There is no mathced data for you in the selected category ${uiState.value.selectedCategory?.name}"
                if(uiState.value.products.isEmpty()){
                    theMes = "Auth to get access to products catalog data"
                }

                _uiState.update { it.copy(emptyDataMes =theMes) }
            }
        }
    }

    private fun filterByCategory(category: Category?) {
        val newPick =  if(category != uiState.value.selectedCategory){ category}else{null}
        val filtered = if (newPick==null) {
            _uiState.value.products
        } else {
            _uiState.value.products.filter { it.category == category?.name }
        }

        _uiState.update { it.copy(selectedCategory = newPick, filteredProducts = filtered) }

        if (filtered.isEmpty()){
            onEvent(ProductCatalogEvents.OnEmptyProducts)
        }
    }
}


