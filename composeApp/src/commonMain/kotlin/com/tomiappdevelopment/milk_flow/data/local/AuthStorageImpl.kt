package com.tomiappdevelopment.milk_flow.data.local


import com.russhwolf.settings.Settings
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.AuthResponse
import com.tomiappdevelopment.milk_flow.domain.models.subModels.AuthData
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

expect object SettingsProvider {
    val settings: Settings
}

class AuthStorageImpl(
    private val settings: Settings
) : AuthStorage {

    private companion object {
        const val KEY_TOKEN = "auth_token"
        const val KEY_REFRESH_TOKEN = "auth_refresh_token"
        const val KEY_USER_ID = "user_id"
    }

    private val authFlow = MutableStateFlow(loadAuthData())

    override fun observeAuthInfo(): Flow<AuthData?> = authFlow

    override suspend fun setAuthInfo(authInfo: AuthData) {
        authInfo.idToken?.let { settings.putString(KEY_TOKEN, it) }
        authInfo.refreshToken?.let { settings.putString(KEY_REFRESH_TOKEN, it) }
        authInfo.localId?.let { settings.putString(KEY_USER_ID, it) }
        authFlow.value = authInfo
    }

    override suspend fun clearAuthInfo() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
        settings.remove(KEY_USER_ID)
        authFlow.value = null
    }

    private fun loadAuthData(): AuthData? {
        val token = settings.getStringOrNull(KEY_TOKEN)
        val refreshToken = settings.getStringOrNull(KEY_REFRESH_TOKEN)
        val userId = settings.getStringOrNull(KEY_USER_ID)

        return if (token != null && refreshToken != null && userId != null) {
            AuthData(idToken = token, refreshToken = refreshToken, localId = userId)
        } else {
            null
        }
    }
}
