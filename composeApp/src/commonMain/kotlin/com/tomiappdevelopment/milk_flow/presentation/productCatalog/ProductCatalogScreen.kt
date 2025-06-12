package com.tomiappdevelopment.milk_flow.presentation.productCatalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.components.CategoriesBar
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.components.ProductDialog
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.components.ProductPreviewItem
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
fun ProductCatalogScreen(productCatalogStatesAndEvents:ProductCatalogStatesAndEvents) {

    val uiState = productCatalogStatesAndEvents.uiState

    val snackBarHostState = remember { SnackbarHostState() }

    // State for the selected product
    var selectedProduct by remember {  mutableStateOf<Product?>(null)}

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {
        LaunchedEffect(productCatalogStatesAndEvents.uiState.uiMessage) {
            productCatalogStatesAndEvents.uiState.uiMessage.consumeAsFlow()
                .collect {
                    snackBarHostState.showSnackbar(
                        it.asString2(),
                        duration = SnackbarDuration.Long
                    )
                }
        }


        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                CategoriesBar(selectedCategory = uiState.selectedCategory, onCategorySelected = {productCatalogStatesAndEvents.onCategorySelected(it)})

                if (uiState.filteredProducts.isEmpty() && uiState.emptyDataMes != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "אין מוצרים להצגה",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = uiState.emptyDataMes ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.filteredProducts) { product ->
                            ProductPreviewItem(
                                product = product,
                                onAddToCartClick = { selectedProduct = product },
                                modifier = Modifier.clickable { selectedProduct = product }
                            )
                        }
                    }
                }
            }

            // Overlay Dialog shown only when a product is selected
            selectedProduct?.let { product ->
                ProductDialog(
                    product = product,
                    onClose = { selectedProduct = null },
                    onAddOrUpdate = { productId, amount ->
                        productCatalogStatesAndEvents.onAddToCart(
                            CartItem(productId,amount)
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}
