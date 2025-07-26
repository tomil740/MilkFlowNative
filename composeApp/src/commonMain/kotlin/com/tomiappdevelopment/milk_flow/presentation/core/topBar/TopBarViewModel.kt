package com.tomiappdevelopment.milk_flow.presentation.core.topBar

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManagerVm
import com.tomiappdevelopment.milk_flow.domain.repositories.CartRepository
import com.tomiappdevelopment.milk_flow.presentation.core.AppRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TopBarViewModel(
    private val authManagerVm: AuthManagerVm,
    private val cartRepository: CartRepository,
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
                    authManagerVm.signOut()
                }
                _uiState.update { it.copy(showLogoutDialog = false) }
            }

            TopBarEvent.CancelLogout -> {
                _uiState.update { it.copy(showLogoutDialog = false) }
            }
            TopBarEvent.RequestLogout -> {
                _uiState.update { it.copy(showLogoutDialog = true) }
            }
        }
    }

    private var _navigationHandler: ((AppRoute) -> Unit)? = null
    fun setNavigationHandler(handler: (AppRoute) -> Unit) {
        _navigationHandler = handler
    }

    init {
        screenModelScope.launch {

            // Add this Firebase warm-up ping
            launch(Dispatchers.IO) {
                authManagerVm.authPing()
            }

            launch {

                authManagerVm.userState.collectLatest { user ->

                    _uiState.update {
                        it.copy(
                            name = user?.name,
                            isLoggedIn = user != null,
                            isDistributor = user?.isDistributer ?: false
                        )
                    }

                    if(user!=null){
                        cartRepository.getCart(user.uid).collectLatest {  cartItems->
                            _uiState.update { it.copy(cartItemCount = cartItems.size) }
                        }
                    }
                }
            }
        }
    }
}
