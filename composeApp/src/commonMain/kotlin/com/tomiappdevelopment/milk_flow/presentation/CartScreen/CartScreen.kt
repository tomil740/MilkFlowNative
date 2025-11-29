package com.tomiappdevelopment.milk_flow.presentation.CartScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.components.CartHeader
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.components.CartPreviewItem
import com.tomiappdevelopment.milk_flow.presentation.core.components.CheckoutButton
import com.tomiappdevelopment.milk_flow.presentation.core.components.EmptyDataMessage
import com.tomiappdevelopment.milk_flow.presentation.core.components.LoadingSpinner
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.components.ProductDialog
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartScreen(cartSatesAndEvents: CartSatesAndEvents
) {
     val uiState =cartSatesAndEvents.uiState

    val snackBarHostState = remember { SnackbarHostState() }

    // State for the selected product
    var selectedProduct by remember { mutableStateOf<CartProduct?>(null) }

    Box {

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            },
            bottomBar = {
                CheckoutButton(
                    loading = false,
                    onClick = { cartSatesAndEvents.onMakeDemand() },
                    label = "בצע הזמנה",
                    enabled = (cartSatesAndEvents.uiState.cartProducts.isNotEmpty()&&cartSatesAndEvents.uiState.authState!=null)
                )
            }

        ) {

            LaunchedEffect(cartSatesAndEvents.uiMessage) {
                cartSatesAndEvents.uiMessage
                    .collectLatest {
                        snackBarHostState.showSnackbar(
                            it.asString2(),
                            duration = SnackbarDuration.Long
                        )
                    }
            }


            Column {
                CartHeader(totalItems = uiState.cartProducts.size)

                AnimatedVisibility(uiState.cartProducts.isEmpty()) {
                    val mes = if(uiState.authState==null){"מתשתמש לא מחובר , התחבר לקבלת מידע"}else{""}
                    EmptyDataMessage(message = mes)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {


                    item {
                        LoadingSpinner(isLoading = uiState.isLoading)
                    }

                    items(uiState.cartProducts, key = {it.product.id}) { item ->
                        CartPreviewItem(
                            cartProduct = item,
                            onEdit = { selectedProduct = it }
                        )
                    }
                }
                // Overlay Dialog shown only when a product is selected
                selectedProduct?.let { product ->
                    ProductDialog(
                        product = product.product,
                        onClose = { selectedProduct = null },
                        initialAmount = product.amount,
                        onAddOrUpdate = { productId, amount ->
                            cartSatesAndEvents.updateItem(
                                CartItem(productId,amount)
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}