package com.tomiappdevelopment.milk_flow.core

import com.tomiappdevelopment.milk_flow.domain.models.User
import com.tomiappdevelopment.milk_flow.domain.models.subModels.AuthData
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthStateProvider
import com.tomiappdevelopment.milk_flow.domain.util.Error
import com.tomiappdevelopment.milk_flow.domain.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock

class AuthManager(
    private val authRepo: AuthRepository,
) : AuthStateProvider {

    private val _authState = MutableStateFlow<AuthData?>(authRepo.getAuthState())
    override val authState: StateFlow<AuthData?> = _authState.asStateFlow()

    private var userStateFlow: StateFlow<User?>? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun userFlow(scope: CoroutineScope): StateFlow<User?> {
        if (userStateFlow == null) {
            userStateFlow = authState
                .mapLatest { authData ->
                    authData?.localId?.let {
                        try {
                            authRepo.getUserObjById(it)
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                .stateIn(
                    scope = scope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null
                )
        }
        return userStateFlow!!
    }


    suspend fun signIn(email: String, password: String): Result<Boolean, Error> {
        val a = authRepo.signIn(email, password)
        println("LoginProcess[3] : Call the sign in http request: ${Clock.System.now()}")
        return when(a){
            is Result.Error<Error> -> {
                 a
            }
            is Result.Success<Boolean> ->{
                println("LoginProcess[4] : sign in http request sucess ${a.data}: ${Clock.System.now()}")
                _authState.update {authRepo.getAuthState() }
                 Result.Success(true)
            }
        }
    }

    suspend fun signOut() {
        _authState.emit(null)
        authRepo.logout()
    }

    override suspend fun authPing(){
        authRepo.authPing()
    }
}
