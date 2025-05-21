package com.tomiappdevelopment.milk_flow.domain.repositories

import com.tomiappdevelopment.milk_flow.domain.models.User
import com.tomiappdevelopment.milk_flow.domain.models.subModels.AuthData
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result


interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Boolean, DataError>
    suspend fun refreshToken(): Result<Boolean, DataError>
    suspend fun logout()
    fun getAuthState(): AuthData?
   suspend fun getUserObjById(uid: String): User?
}
