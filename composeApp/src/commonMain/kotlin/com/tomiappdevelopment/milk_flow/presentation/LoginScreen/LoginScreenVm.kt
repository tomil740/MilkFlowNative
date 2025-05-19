package com.tomiappdevelopment.milk_flow.presentation.LoginScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.data.remote.AuthService
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.AuthResponse
import com.tomiappdevelopment.milk_flow.domain.util.DataError
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

class LoginViewModel(private val authService: AuthService) : ScreenModel {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")

    init {
        screenModelScope.launch {

            withContext(Dispatchers.IO) {
                val a =authService.signIn("someMail@gmail.com", password = "1234")
                println("res $a")
                val b = authService.signIn("shvprslshly@mail.com", password = "1234567")

                println("@@@@@@working $b")

                when(b){
                    is Result.Error<DataError> ->                 println("@@@@@@working ${b.error}")

                    is Result.Success<AuthResponse> ->                println("@@@@@@working ${b.data}")

                }
            }


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

            delay(1000) // simulate network

            // You’d actually call your AuthRepository here.
            val success = _uiState.value.email == "demo@demo.com" && _uiState.value.password == "1234"

            if (success) {
                _uiState.update { it.copy(isLoading = false) }
                // Delegate to AuthManager or call a callback
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "פרטי התחברות שגויים"
                    )
                }
            }
        }
    }
}
