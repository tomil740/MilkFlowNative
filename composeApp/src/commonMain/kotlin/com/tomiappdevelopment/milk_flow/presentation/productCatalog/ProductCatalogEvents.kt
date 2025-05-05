package com.tomiappdevelopment.milk_flow.presentation.productCatalog

import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.models.Category

sealed interface ProductCatalogEvents {
    data class OnCategorySelected(val category: Category?) : ProductCatalogEvents
    data class AddToCart(val cartItem: CartItem) : ProductCatalogEvents
    data object OnEmptyProducts:ProductCatalogEvents
    data object Refresh : ProductCatalogEvents
}