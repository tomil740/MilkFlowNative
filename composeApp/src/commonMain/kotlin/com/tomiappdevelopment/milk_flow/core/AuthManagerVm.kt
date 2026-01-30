package com.tomiappdevelopment.milk_flow.core

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.tomiappdevelopment.milk_flow.domain.models.User
import com.tomiappdevelopment.milk_flow.domain.models.subModels.AuthData
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthStateProvider
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class AuthManagerVm(
    private val authRepo: AuthRepository,
) : ScreenModel, AuthStateProvider {

    private val _authState = MutableStateFlow<AuthData?>(null)
    override val authState: StateFlow<AuthData?> = _authState.asStateFlow()

    private val _userState = MutableStateFlow<User?>(null)
    override val userState: StateFlow<User?> = _userState.asStateFlow()

    init {
        screenModelScope.launch {
            authRepo.getAuthState().collectLatest { authData ->
                _authState.update { authData }

                val user = authData?.localId?.let {
                    try {
                        authRepo.getUserObjById(it)
                    } catch (e: Exception) {
                        null
                    }
                }

                _userState.update { user }
            }
        }
    }

    suspend fun signIn(email: String, password: String): Result<Boolean, Error> {
        val result = authRepo.signIn(email, password)
        println("LoginProcess[3] : Sign-in call: ${Clock.System.now()}")
        return when (result) {
            is Result.Error -> result
            is Result.Success -> {
                println("LoginProcess[4] : Success: ${result.data} at ${Clock.System.now()}")
                Result.Success(true)
            }
        }
    }

    suspend fun signOut() {
        authRepo.logout()
    }

    override suspend fun authPing() {
        authRepo.authPing()
    }
}
