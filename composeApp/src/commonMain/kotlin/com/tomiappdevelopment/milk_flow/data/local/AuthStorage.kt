package com.tomiappdevelopment.milk_flow.data.local

interface AuthStorage {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun saveUserId(userId: String)
    suspend fun clear()

    suspend fun getUserId(): String?
    suspend fun getAccessToken(): String?
}
