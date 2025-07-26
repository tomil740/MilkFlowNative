package com.tomiappdevelopment.milk_flow.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tomiappdevelopment.milk_flow.domain.models.subModels.AuthData
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthStorageImplAndroid(
    private val dataStore: DataStore<Preferences>
) : AuthStorage {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val REFRESH_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    override suspend fun setAuthInfo(authInfo: AuthData) {
        dataStore.edit { prefs ->
            authInfo.idToken?.let { prefs[TOKEN_KEY] = it } ?: prefs.remove(TOKEN_KEY)
            authInfo.refreshToken?.let { prefs[REFRESH_KEY] = it } ?: prefs.remove(REFRESH_KEY)
            authInfo.localId?.let { prefs[USER_ID_KEY] = it } ?: prefs.remove(USER_ID_KEY)
        }
    }

    override fun observeAuthInfo(): Flow<AuthData?> {
        return dataStore.data.map{ prefs ->
                val token = prefs[TOKEN_KEY]
                val refresh = prefs[REFRESH_KEY]
                val userId = prefs[USER_ID_KEY]

                // If all are null, treat as no auth
                if (token == null && refresh == null && userId == null) {
                    null
                } else {
                    AuthData(idToken = token, refresh, userId)
                }
            }
    }

    override suspend fun clearAuthInfo() {
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(REFRESH_KEY)
            prefs.remove(USER_ID_KEY)
        }
    }
}
