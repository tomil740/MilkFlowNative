package com.tomiappdevelopment.milk_flow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.tomiappdevelopment.milk_flow.core.presentation.AppTheme
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.CartScreenClass
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.DemandsMangerScreenClass
import com.tomiappdevelopment.milk_flow.presentation.LoginScreen.LoginScreenClass
import com.tomiappdevelopment.milk_flow.presentation.core.AppRoute
import com.tomiappdevelopment.milk_flow.presentation.core.components.LogoutDialog
import com.tomiappdevelopment.milk_flow.presentation.core.topBar.TopBar
import com.tomiappdevelopment.milk_flow.presentation.core.topBar.TopBarEvent
import com.tomiappdevelopment.milk_flow.presentation.core.topBar.TopBarViewModel
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.ProductCatalogScreenClass
import org.koin.compose.rememberKoinInject


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
    //will be flag to the platform on android set dynamicColor to true
    dynamicColor: Boolean = false,
) {
    val topBarPadding = if(dynamicColor){8.dp}else{45.dp}

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
                        else -> navigator.replaceAll(ProductCatalogScreenClass())
                    }
                }
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopBar(
                        state = uiState,
                        onNavigate = { viewModel.onEvent(TopBarEvent.Navigate(it)) },
                        onLogout = { viewModel.onEvent(TopBarEvent.RequestLogout) } ,// trigger dialog
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .padding(top = topBarPadding)
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

                    // 👇 Logout Confirmation Dialog
                    if (uiState.showLogoutDialog) {
                        LogoutDialog(
                            onConfirm = { viewModel.onEvent(TopBarEvent.Logout) },
                            onDismiss = { viewModel.onEvent(TopBarEvent.CancelLogout) }
                        )
                    }
                }
            }
        }
    }
}
