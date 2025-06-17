package com.tomiappdevelopment.milk_flow.presentation.core.topBar

import com.tomiappdevelopment.milk_flow.presentation.core.AppRoute

sealed class TopBarEvent {
    data class Navigate(val route: AppRoute) : TopBarEvent()
    object RequestLogout : TopBarEvent()
    object CancelLogout : TopBarEvent()
    object Logout : TopBarEvent()
}
