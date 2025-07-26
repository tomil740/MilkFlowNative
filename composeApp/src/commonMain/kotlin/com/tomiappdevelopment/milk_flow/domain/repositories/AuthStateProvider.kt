package com.tomiappdevelopment.milk_flow.domain.repositories

import com.tomiappdevelopment.milk_flow.domain.models.User
import com.tomiappdevelopment.milk_flow.domain.models.subModels.AuthData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthStateProvider {
    val authState: StateFlow<AuthData?>
    val userState: StateFlow<User?>
    suspend fun authPing()
}
