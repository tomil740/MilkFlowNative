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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val authStorage: AuthStorage,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<Boolean, DataError> {
        return withContext(Dispatchers.IO) {
            try {
                when (val response = authService.signIn(email, password)) {
                    is Result.Error<DataError> -> Result.Error(response.error)

                    is Result.Success<AuthResponse> -> {
                        println("LoginProcess[1] : first service response : ${Clock.System.now()}")
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
    }

    override suspend fun refreshToken(): Result<Boolean, DataError> {
        // Will be implemented later
        TODO()
    }

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            authStorage.clearAuth()
        }
    }

    override fun getAuthState(): AuthData? {
        println("LoginProcess[3] : get the auth state from authStorage(local) : ${Clock.System.now()}")
        // Fast and safe enough to keep on main thread (likely just SharedPreferences or memory)
        val a = authStorage.getAuth()
        return if (a != null) {
            println("LoginProcess[4] : authStorage upadate : ${Clock.System.now()}")
            AuthData(idToken = a.idToken, refreshToken = a.refreshToken, localId = a.localId)
        } else null
    }

    override suspend fun getUserObjById(uid: String): User? {
        println("LoginProcess[7] : get user by id: ${Clock.System.now()}")
        return withContext(Dispatchers.IO) {
            // 1. Try to get from local cache
            val cachedEntity = userDao.getUserById(uid)
            if (cachedEntity != null) {
                println("LoginProcess[8] : get user by id: ${Clock.System.now()}")
                return@withContext cachedEntity.toUserDomain()
            }

            // 2. Fetch from remote
            when (val remoteResult = authService.getUserById(uid)) {
                is Result.Success -> {
                    val user = remoteResult.data
                    userDao.insertUser(user.toEntity())
                    user
                }
                is Result.Error -> {
                    println("user by id fetch error ${remoteResult.error}")
                    null
                }
            }
        }
    }

    override suspend fun authPing() {
        val dummyEmail = "warmup@fake.com"
        val dummyPassword = "warmup123"

        val result = signIn(dummyEmail, dummyPassword)
        when (result) {
            is Result.Success -> println("Ping succeeded unexpectedly")
            is Result.Error -> println("Ping failed as expected, warming backend")
        }
    }
}
