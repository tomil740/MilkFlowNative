package com.tomiappdevelopment.milk_flow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.tomiappdevelopment.milk_flow.core.presentation.AppTheme
import com.tomiappdevelopment.milk_flow.presentation.LoginScreen.LoginScreenClass
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.ProductCatalogScreenClass
import com.tomiappdevelopment.milk_flow.presentation.core.TopBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen

object NavigationManager {
    var navigator: Navigator? = null

    fun navigateTo(screen: Screen) {
        navigator?.push(screen)
    }

    fun pop() {
        navigator?.pop()
    }

    fun replace(screen: Screen) {
        navigator?.replace(screen)
    }
}

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

                Navigator(ProductCatalogScreenClass()) { navigator ->
                    NavigationManager.navigator = navigator
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = { TopBar(false, false, 11, onNavigate = {}, {}) }
                    ) { innerPadding ->
                        // Apply the padding to prevent overlapping
                        Box(modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 0.dp)
                            .fillMaxSize()
                        ) {
                            CurrentScreen()

                        }
                }

        }
    }
}
