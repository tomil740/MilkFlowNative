package com.tomiappdevelopment.milk_flow.presentation.productCatalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.components.ProductDialog
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.components.ProductPreviewItem

@Composable
fun ProductCatalogScreen() {
    // State for the selected product
    var selectedProduct by remember {  mutableStateOf<Product?>(null)}

    val items1 = remember {
        mutableListOf<Product>().apply {
            add(Product(1, 1, "someName1", "https://tomiappdevelopment.netlify.app/images/profileImg.JPG", "dsfsdf", 12, 2, "desc"))
            repeat(20) { i ->
                add(Product(i + 2, 1, "ריקוטה 250 גרם בדץ $i", "https://milkflow.netlify.app/productsImages/regular/7290005992735.webp", "dsfsdf", 12, 2, "desc"))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items1) { product ->
                ProductPreviewItem(
                    product = product,
                    onAddToCartClick = { selectedProduct = product }
                )
            }
        }

        // Overlay Dialog shown only when a product is selected
        selectedProduct?.let { product ->
            ProductDialog(
                product = product,
                onClose = { selectedProduct = null },
                addToCart = { productId, amount ->
                    // TODO: Handle add to cart logic here if needed
                    println("Add to cart: $productId x$amount")
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
