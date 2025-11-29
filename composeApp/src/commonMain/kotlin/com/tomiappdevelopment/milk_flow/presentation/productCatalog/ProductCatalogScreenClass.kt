package com.tomiappdevelopment.milk_flow.presentation.productCatalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.Category
import kotlinx.coroutines.flow.Flow

class ProductCatalogScreenClass():Screen {
    @Composable
    override fun Content() {
        val a = getScreenModel<ProductCatalogVm>()
        val state by a.uiState.collectAsState()
        val b = ProductCatalogStatesAndEvents(
            uiState = state,
            uiMessage = a.uiMessage,
            onCategorySelected = {a.onEvent(ProductCatalogEvents.OnCategorySelected(it))},
            onAddToCart = {a.onEvent(ProductCatalogEvents.AddToCart(it))}
        )

        ProductCatalogScreen(
              productCatalogStatesAndEvents = b,
        )

    }

}

data class ProductCatalogStatesAndEvents(
    val uiState: ProductCatalogUiState,
    val uiMessage: Flow<UiText>,
    val onCategorySelected: (Category?)->Unit,
    val onAddToCart: (CartItem)-> Unit
)