package com.tomiappdevelopment.milk_flow.presentation.core.topBar

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManager
import com.tomiappdevelopment.milk_flow.presentation.core.AppRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TopBarViewModel(
    private val authManager: AuthManager
) : ScreenModel {

    private val _uiState = MutableStateFlow(TopBarUiState())
    val uiState: StateFlow<TopBarUiState> = _uiState

    fun onEvent(event: TopBarEvent) {
        when (event) {
            is TopBarEvent.Navigate -> {
                _navigationHandler?.invoke(event.route)
            }
            is TopBarEvent.Logout -> {
                // Handle logout action here
                screenModelScope.launch {
                    authManager.signOut()
                }
            }
        }
    }

    private var _navigationHandler: ((AppRoute) -> Unit)? = null
    fun setNavigationHandler(handler: (AppRoute) -> Unit) {
        _navigationHandler = handler
    }

    init {
        screenModelScope.launch {
            authManager.userFlow(this).collect { user ->
                println("!!!!!@@!@#!@#the user update!!@!!@ $user")
                _uiState.update {
                    it.copy(
                        name = user?.name,
                        isLoggedIn = user != null,
                        isDistributor = user?.isDistributer ?: false
                    )
                }
            }
        }
    }
}
