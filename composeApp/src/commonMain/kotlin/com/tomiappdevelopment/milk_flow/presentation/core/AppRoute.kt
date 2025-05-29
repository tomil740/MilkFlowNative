package com.tomiappdevelopment.milk_flow.presentation.core

sealed class AppRoute {
    object Login : AppRoute()
    object ProductsCatalog : AppRoute()
    object Cart: AppRoute()
}
