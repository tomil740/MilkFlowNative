package com.tomiappdevelopment.milk_flow.presentation.productCatalog

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManagerVm
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.models.Category
import com.tomiappdevelopment.milk_flow.domain.models.ProductMetadata
import com.tomiappdevelopment.milk_flow.domain.repositories.CartRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.usecase.GetAuthorizedProducts
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncIfNeededUseCase
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ProductCatalogVm(
    private val productsRepo: ProductRepository,
    private val syncIfNeededUseCase:SyncIfNeededUseCase,
    private val getAuthorizedProducts:GetAuthorizedProducts,
    private val authManagerVm: AuthManagerVm,
    private val cartRepository: CartRepository
    ): ScreenModel{

    private val _uiMessage = MutableSharedFlow<UiText>()
    val uiMessage = _uiMessage.asSharedFlow()

    private val _uiState = MutableStateFlow(
        ProductCatalogUiState()
    )
    private var syncJob: Job? = null
    private val fullCacheEmptyFlag = MutableStateFlow<Boolean>(false)

    //the observable stateflow ui state that is listening to the original ui state
    var uiState = _uiState.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value)

    init {
        screenModelScope.launch {

            launch {
                authManagerVm.authState.collect { authRes->
                    _uiState.update { it.copy(authState=(authRes?.localId)) }
                }
            }
            launch {
                launchInitialSync()
            }

            launch {
                fullCacheEmptyFlag.collectLatest { theFlag->
                    if(theFlag){
                        launchInitialSync(true)
                    }
                }
            }



            launch {
                getAuthorizedProducts.invoke().collect { products ->
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

    private fun launchInitialSync(forceResetMetaData: Boolean = false) {
        if (syncJob?.isActive == true) return

        syncJob = screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            if (forceResetMetaData) {
                productsRepo.setProductLocalMetaData(ProductMetadata())
            }

            when (val result = syncIfNeededUseCase.invoke()) {
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiMessage.emit(UiText.DynamicString(result.error.toString()))
                }

                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    if (result.data) {
                        _uiMessage.emit(UiText.DynamicString("Successfully synced"))
                    }
                }
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


