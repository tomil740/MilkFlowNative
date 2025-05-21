package com.tomiappdevelopment.milk_flow.data.repositories

import com.tomiappdevelopment.milk_flow.data.local.AuthStorage
import com.tomiappdevelopment.milk_flow.data.local.dao.UserDao
import com.tomiappdevelopment.milk_flow.data.remote.AuthService
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.AuthResponse
import com.tomiappdevelopment.milk_flow.data.util.toEntity
import com.tomiappdevelopment.milk_flow.data.util.toUserDomain
import com.tomiappdevelopment.milk_flow.domain.models.User
import com.tomiappdevelopment.milk_flow.domain.models.subModels.AuthData
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthRepository
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.domain.util.mapHttpResponse

class AuthRepositoryImpl(
    private val authService: AuthService, // Handles API calls
    private val authStorage: AuthStorage,  // Handles caching
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<Boolean, DataError> {
        return try {
            val response = authService.signIn(email, password)

            when(response){
                is Result.Error<DataError> -> {
                    return Result.Error(response.error)
                }
                is Result.Success<AuthResponse> -> {

                    authStorage.saveAuth(
                        idToken = response.data.idToken,
                        refreshToken = response.data.refreshToken,
                        localId = response.data.localId
                    )


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

    override fun getAuthState(): AuthData? {
        val a = authStorage.getAuth()
        return if(a!=null){
            AuthData(idToken = a.idToken, refreshToken = a.refreshToken, localId = a.localId)
        }else  null
    }

    override suspend fun getUserObjById(uid: String): User? {
        println("%%%!!%%Called@@@")
        // 1. Try to get from local cache
        val cachedEntity = userDao.getUserById(uid)
        if (cachedEntity != null) {
            println("%%%!!%%Avialble $cachedEntity")
            return cachedEntity.toUserDomain() // convert DB entity to domain model User
        }

        // 2. Fetch from remote
        val remoteResult = authService.getUserById(uid)
        println("%%%!!%% get remote user $remoteResult")
        return when (remoteResult) {
            is Result.Success -> {
                val user = remoteResult.data
                // 3. Cache to local DB
                userDao.insertUser(user.toEntity())
                user
            }
            is Result.Error -> {
                // handle error or return null
                null
            }
        }
    }
}
