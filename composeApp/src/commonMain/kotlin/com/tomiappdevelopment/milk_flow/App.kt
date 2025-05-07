package com.tomiappdevelopment.milk_flow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.tomiappdevelopment.milk_flow.core.presentation.AppTheme
import com.tomiappdevelopment.milk_flow.di.appModule
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.ProductCatalogScreen
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.ProductCatalogScreenClass
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.startKoin

@Composable
@Preview
fun App(
    darkTheme: Boolean =false,
    dynamicColor: Boolean=true,
) {
    //todo : need to figure out how that should be realy solve (init / some intalize function ...)

    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor
    ) {

        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Navigator(ProductCatalogScreenClass())
        }
    }
}
