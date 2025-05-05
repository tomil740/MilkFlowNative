package com.tomiappdevelopment.milk_flow.presentation.productCatalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.tomiappdevelopment.milk_flow.domain.models.Category

class ProductCatalogScreenClass():Screen {
    @Composable
    override fun Content() {
        val a = getScreenModel<ProductCatalogVm>()
        val state by a.uiState.collectAsState()
        val b = ProductCatalogStatesAndEvents(state, onCategorySelected = {a.onEvent(ProductCatalogEvents.OnCategorySelected(it))})

        ProductCatalogScreen(
              productCatalogStatesAndEvents = b,
        )

    }

}

data class ProductCatalogStatesAndEvents(
    val uiState: ProductCatalogUiState,
    val onCategorySelected: (Category?)->Unit
)