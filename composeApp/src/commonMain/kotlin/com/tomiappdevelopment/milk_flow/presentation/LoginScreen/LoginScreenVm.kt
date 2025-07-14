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
import kotlinx.datetime.Clock

class LoginViewModel(private val authManager: AuthManager) : ScreenModel {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            authManager.userFlow(screenModelScope)
        }
    }

    fun onPhoneChange(newPhone: String) {
        _uiState.update { it.copy(phoneNumber = newPhone, phoneNumberError = null, errorMessage = null) }
        validateForm()
    }


    private fun validateForm() {
        val phone = _uiState.value.phoneNumber.trim()

        val phoneError = if (!isValidPhoneNumber(phone)) "מספר טלפון לא תקין" else null
        val isFormValid = phoneError == null

        _uiState.update {
            it.copy(
                phoneNumberError = phoneError,
                isFormValid = isFormValid
            )
        }
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.length == 10 && phone.all { it.isDigit() } && phone.startsWith("05")
    }

    fun onLoginClicked() {
        println("LoginProcess[0] : Login clicked Start!!: ${Clock.System.now()}")
        if (!_uiState.value.isFormValid || _uiState.value.isLoading) return

        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val email = "${_uiState.value.phoneNumber}@mail.com"
            val password = _uiState.value.phoneNumber

            val a = withContext(Dispatchers.IO) {
                authManager.signIn(email, password)
            }
            println("TOMI_TRACE SignIn result:$a  ${Clock.System.now()}")

            when(a){
                is Result.Error<Error> ->  _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "פרטי התחברות שגויים ${a.error}"
                    )
                }
                is Result.Success<Boolean> -> {
                    _uiState.update { it.copy(showSuccessDialog = true,isLoading = false) }
                }
            }
        }
    }

}
