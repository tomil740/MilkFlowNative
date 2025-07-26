package com.tomiappdevelopment.milk_flow.data.local


import com.russhwolf.settings.Settings
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.AuthResponse
import com.tomiappdevelopment.milk_flow.domain.models.subModels.AuthData
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthStorage
import kotlinx.coroutines.flow.Flow

expect object SettingsProvider {
    val settings: Settings
}

class AuthStorageImpl(settings1: Settings): AuthStorage {
    private val settings = settings1
    private  val KEY_ID_TOKEN = "auth_id_token"
    private  val KEY_REFRESH_TOKEN = "auth_refresh_token"
    private  val KEY_LOCAL_ID = "auth_local_id"

    fun saveAuth(idToken: String, refreshToken: String, localId: String) {
        settings.putString(KEY_ID_TOKEN, idToken)
        settings.putString(KEY_REFRESH_TOKEN, refreshToken)
        settings.putString(KEY_LOCAL_ID, localId)
    }

    fun getAuth(): AuthResponse? {
        val idToken = settings.getStringOrNull(KEY_ID_TOKEN)
        val refreshToken = settings.getStringOrNull(KEY_REFRESH_TOKEN)
        val localId = settings.getStringOrNull(KEY_LOCAL_ID)
        return if (idToken != null && refreshToken != null && localId != null) {
            AuthResponse(idToken, refreshToken, localId)
        } else null
    }

    override fun observeAuthInfo(): Flow<AuthData?> {
        TODO("Not yet implemented")
    }

    override suspend fun setAuthInfo(authInfo: AuthData) {
        TODO("Not yet implemented")

    }

    override suspend fun clearAuthInfo() {
        settings.remove(KEY_ID_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
        settings.remove(KEY_LOCAL_ID)
    }
}

