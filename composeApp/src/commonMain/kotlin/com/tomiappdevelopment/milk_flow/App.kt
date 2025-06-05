package com.tomiappdevelopment.milk_flow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.tomiappdevelopment.milk_flow.core.presentation.AppTheme
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.CartScreenClass
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.DemandsMangerScreen
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.DemandsMangerScreenClass
import com.tomiappdevelopment.milk_flow.presentation.LoginScreen.LoginScreenClass
import com.tomiappdevelopment.milk_flow.presentation.core.AppRoute
import com.tomiappdevelopment.milk_flow.presentation.core.topBar.TopBar
import com.tomiappdevelopment.milk_flow.presentation.core.topBar.TopBarViewModel
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.ProductCatalogScreenClass
import org.koin.compose.rememberKoinInject
import com.tomiappdevelopment.milk_flow.presentation.core.topBar.TopBarEvent


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
fun App(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
) {


    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor
    ) {
        Navigator(ProductCatalogScreenClass()) { navigator ->
            NavigationManager.navigator = navigator

            val viewModel = rememberKoinInject<TopBarViewModel>()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.setNavigationHandler { route: AppRoute ->
                    when (route) {
                        AppRoute.Login -> navigator.replaceAll(LoginScreenClass())
                        AppRoute.ProductsCatalog -> navigator.replaceAll(ProductCatalogScreenClass())
                        AppRoute.Cart -> navigator.replaceAll(CartScreenClass())
                        AppRoute.DemandsManger -> navigator.replaceAll(DemandsMangerScreenClass())
                    }
                }
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopBar(
                        state = uiState,
                        onNavigate = {viewModel.onEvent(TopBarEvent.Navigate(it))},
                        onLogout = {viewModel.onEvent(TopBarEvent.Logout)}
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
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
