package com.tomiappdevelopment.milk_flow.presentation.core.topBar

import com.tomiappdevelopment.milk_flow.presentation.core.AppRoute

sealed class TopBarEvent {
    data class Navigate(val route: AppRoute) : TopBarEvent()
    object Logout : TopBarEvent()
}
