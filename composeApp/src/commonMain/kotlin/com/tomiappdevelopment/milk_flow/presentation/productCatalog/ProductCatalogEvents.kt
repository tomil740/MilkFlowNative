package com.tomiappdevelopment.milk_flow.presentation.productCatalog

import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.models.Category

sealed interface ProductCatalogUiEvent {
    data class OnProductClicked(val product: Product) : ProductCatalogUiEvent
    data class OnCategorySelected(val category: Category) : ProductCatalogUiEvent
    data class AddToCart(val productId: Int) : ProductCatalogUiEvent
    object Refresh : ProductCatalogUiEvent
}