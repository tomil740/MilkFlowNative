package com.tomiappdevelopment.milk_flow.presentation.LoginScreen

data class LoginUiState(
    val phoneNumber:String="",
    val phoneNumberError: String? = null,
    val isLoading: Boolean = false,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false,

    )
