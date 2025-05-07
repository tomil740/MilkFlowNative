package com.tomiappdevelopment.milk_flow.presentation.productCatalog

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.models.Category
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.ProductCatalogEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProductCatalogVm(productsRepo: ProductRepository): ScreenModel{

    private val uiMessage = Channel<UiText>()


    private val _uiState = MutableStateFlow(
        ProductCatalogUiState(
            uiMessage = uiMessage
        )
    )
    //the observable stateflow ui state that is listening to the original ui state
    var uiState = _uiState.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value)

    init {
        screenModelScope.launch {
            withContext(Dispatchers.IO) {
                delay(1000)
                _uiState.update { it.copy(isLoading = true) }
                if(uiState.value.products.isEmpty()) {
                    val a = productsRepo.syncProductData()

                    when (a) {
                        is Result.Error<*> -> {
                            _uiState.update { it.copy(isLoading = false) }
                            uiMessage.send(UiText.DynamicString(a.error.toString()))
                        }

                        is Result.Success<*> -> {
                            _uiState.update { it.copy(isLoading = false) }
                            uiMessage.send(UiText.DynamicString("Sessfuly synced"))
                        }
                    }
                }else{
                    _uiState.update { it.copy(isLoading = false) }
                    uiMessage.send(UiText.DynamicString("Local data is in sync!!"))
                }
            }
        }

        screenModelScope.launch {
            productsRepo.getProducts().collect{ products->
                _uiState.update { it.copy(products = products) }
                filterByCategory(uiState.value.selectedCategory)
                if (uiState.value.filteredProducts.isEmpty()){
                    onEvent(ProductCatalogEvents.OnEmptyProducts)
                }
            }
        }

    }

    fun onEvent(event:ProductCatalogEvents) {
        when(event){
            is ProductCatalogEvents.AddToCart -> TODO()
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


