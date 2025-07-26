package com.tomiappdevelopment.milk_flow.domain.repositories

import com.tomiappdevelopment.milk_flow.domain.models.subModels.AuthData
import kotlinx.coroutines.flow.Flow

// commonMain
interface AuthStorage {
    fun observeAuthInfo(): Flow<AuthData?>
    suspend fun setAuthInfo(authInfo:AuthData)
    suspend fun clearAuthInfo()
}
