package com.tomiappdevelopment.milk_flow.presentation.productCatalog

import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.models.Category
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.models.User
import kotlinx.coroutines.channels.Channel

data class ProductCatalogUiState(
    val products: List<Product> = emptyList(),                // Full list from repo
    val authState: String? = null,
    val filteredProducts: List<Product> = emptyList(),        // List shown to user
    val selectedCategory: Category? = null,                   // Current category filter
    val selectedProduct: Product? = null,                     // For product detail or dialog
    val isLoading: Boolean = false,                           // Loading state
    val emptyDataMes:String? = null,
    val uiMessage : Channel<UiText>,
)
