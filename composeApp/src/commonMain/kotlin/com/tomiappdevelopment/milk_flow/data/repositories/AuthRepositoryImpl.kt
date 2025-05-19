package com.tomiappdevelopment.milk_flow.data.repositories

import com.tomiappdevelopment.milk_flow.data.local.AuthStorage
import com.tomiappdevelopment.milk_flow.data.remote.AuthService
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.AuthResponse
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthRepository
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.domain.util.mapHttpResponse

class AuthRepositoryImpl(
    private val authService: AuthService, // Handles API calls
  //  private val authStorage: AuthStorage  // Handles caching
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<Boolean, DataError> {
        return try {
            val response = authService.signIn(email, password) // API call

            when(response){
                is Result.Error<DataError> -> {
                    return Result.Error(response.error)
                }
                is Result.Success<AuthResponse> -> {
                    println("wroking ${response.data.toString()}")
                    /*
                    authStorage.saveTokens(
                        accessToken = response.data.idToken,
                        refreshToken = response.data.refreshToken
                    )
                    authStorage.saveUserId(response.data.localId)

                     */
                    Result.Success(true)
                }
            }

        } catch (e: Exception) {
            Result.Error(mapHttpResponse(e))
        }
    }

    override suspend fun refreshToken(): Result<Boolean, DataError>{
        // Will be implemented later
        TODO()
    }

    override suspend fun logout() {
     //   authStorage.clear()
    }
}
