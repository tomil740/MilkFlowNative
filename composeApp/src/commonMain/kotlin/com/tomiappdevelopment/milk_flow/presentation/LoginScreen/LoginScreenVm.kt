package com.tomiappdevelopment.milk_flow.presentation.LoginScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.core.AuthManager
import com.tomiappdevelopment.milk_flow.data.remote.AuthService
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.AuthResponse
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val authManager: AuthManager) : ScreenModel {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")

    init {
        screenModelScope.launch {
            authManager.userFlow(screenModelScope)
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, emailError = null, errorMessage = null) }
        validateForm()
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, passwordError = null, errorMessage = null) }
        validateForm()
    }

    private fun validateForm() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        val emailError = if (!emailRegex.matches(email)) "אימייל לא תקין" else null
        val passwordError = if (password.isBlank()) "יש להזין סיסמה" else null

        val isFormValid = emailError == null && passwordError == null

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                isFormValid = isFormValid
            )
        }
    }
    fun onLoginClicked() {
        if (!_uiState.value.isFormValid || _uiState.value.isLoading) return

        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // You’d actually call your AuthRepository here.
            val a = authManager.signIn(uiState.value.email,uiState.value.password)

            when(a){
                is Result.Error<Error> ->  _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "פרטי התחברות שגויים ${a.error}"
                    )
                }
                is Result.Success<Boolean> -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "התחבר בהצלחה! ${a.data}"
                        )
                    }
                }
            }

        }
    }
}
