package com.tomiappdevelopment.milk_flow.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tomiappdevelopment.milk_flow.data.local.crypto.CryptoManager
import com.tomiappdevelopment.milk_flow.domain.models.subModels.AuthData
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import android.util.Log
import javax.crypto.AEADBadTagException

class AuthStorageImplAndroid(
    private val dataStore: DataStore<Preferences>,
    private val cryptoManager: CryptoManager
) : AuthStorage {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val REFRESH_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private const val TAG = "AuthStorageImplAndroid"
    }

    override suspend fun setAuthInfo(authInfo: AuthData) {
        dataStore.edit { prefs ->
            authInfo.idToken?.let {
                prefs[TOKEN_KEY] = cryptoManager.encrypt(it)
            } ?: prefs.remove(TOKEN_KEY)

            authInfo.refreshToken?.let {
                prefs[REFRESH_KEY] = cryptoManager.encrypt(it)
            } ?: prefs.remove(REFRESH_KEY)

            authInfo.localId?.let {
                prefs[USER_ID_KEY] = cryptoManager.encrypt(it)
            } ?: prefs.remove(USER_ID_KEY)
        }
    }

    /**
     * Safely decrypts encrypted data. Returns null if decryption fails
     * (e.g., data encrypted with different key from previous install).
     */
    private fun safeDecrypt(encrypted: String?): String? {
        if (encrypted == null) return null
        return try {
            cryptoManager.decrypt(encrypted)
        } catch (e: AEADBadTagException) {
            // Data was encrypted with a different key (likely from previous install)
            Log.w(TAG, "Decryption failed - data encrypted with different key, treating as no data", e)
            null
        } catch (e: Exception) {
            // Any other decryption error - treat as corrupted data
            Log.w(TAG, "Decryption failed, treating as no data", e)
            null
        }
    }

    override fun observeAuthInfo(): Flow<AuthData?> {
        return dataStore.data
            .catch { e ->
                // If DataStore itself has an error, clear all data and return empty
                Log.e(TAG, "Error reading DataStore, clearing corrupted data", e)
                clearAuthInfo()
                emit(androidx.datastore.preferences.core.emptyPreferences())
            }
            .map { prefs ->
                // Try to decrypt each value safely
                val token = safeDecrypt(prefs[TOKEN_KEY])
                val refresh = safeDecrypt(prefs[REFRESH_KEY])
                val userId = safeDecrypt(prefs[USER_ID_KEY])

                // If we had encrypted data but decryption failed, clear the corrupted entries
                var needsCleanup = false
                if (prefs[TOKEN_KEY] != null && token == null) {
                    needsCleanup = true
                }
                if (prefs[REFRESH_KEY] != null && refresh == null) {
                    needsCleanup = true
                }
                if (prefs[USER_ID_KEY] != null && userId == null) {
                    needsCleanup = true
                }

                // Clean up corrupted data if needed
                if (needsCleanup) {
                    dataStore.edit { editPrefs ->
                        if (prefs[TOKEN_KEY] != null && token == null) {
                            editPrefs.remove(TOKEN_KEY)
                        }
                        if (prefs[REFRESH_KEY] != null && refresh == null) {
                            editPrefs.remove(REFRESH_KEY)
                        }
                        if (prefs[USER_ID_KEY] != null && userId == null) {
                            editPrefs.remove(USER_ID_KEY)
                        }
                    }
                }

                // Return auth data if we have any valid decrypted values
                if (token == null && refresh == null && userId == null) {
                    null
                } else {
                    AuthData(idToken = token, refreshToken = refresh, localId = userId)
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
