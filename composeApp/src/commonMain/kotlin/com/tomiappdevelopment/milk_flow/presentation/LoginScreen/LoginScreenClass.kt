package com.tomiappdevelopment.milk_flow.presentation.LoginScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class LoginScreenClass():Screen {
    @Composable
    override fun Content() {
        val a = getScreenModel<LoginViewModel>()
        val state by a.uiState.collectAsState()
        val b = LoginStatesAndEvents(state, onLogin = {a.onLoginClicked()}, onEmailChange = {a.onEmailChange(it)}, onPassWordChange = {a.onPasswordChange(it)})


        LoginScreen(
            b
        )

    }

}

data class LoginStatesAndEvents(
    val uiState: LoginUiState,
    val onLogin: ()-> Unit,
    val onEmailChange: (String)-> Unit,
    val onPassWordChange: (String)-> Unit

)
