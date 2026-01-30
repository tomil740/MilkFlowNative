package com.tomiappdevelopment.milk_flow.data.local

import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Singleton store for last demand sync timestamp.
 * Uses Settings (KMP-compatible key-value) same pattern as auth.
 * All logic and API-format handling lives here; safe fallback to 55h on any edge case.
 */
object DemandSyncStore {

    private val settings: Settings get() = SettingsProvider.settings

    private const val KEY_LAST_DEMAND_SYNC = "last_demand_sync_timestamp"

    private const val RECOVERY_HOURS_MS = 55L * 60 * 60 * 1000
    private const val SET_BUFFER_MS = 60 * 1000L

    /**
     * Returns the timestamp string to use in the API request (updateAt >= this).
     * If stored value is within last 55h → return it; else return (now - 55h) in API format.
     * On null, parse error, or stale value → fallback to 55h. Thread-safe.
     */
    fun getDemandsLastSync(): String {
        val fallback = fallbackTimestamp()
        val stored = settings.getStringOrNull(KEY_LAST_DEMAND_SYNC) ?: return fallback
        if (stored.isBlank()) return fallback
        return try {
            val storedInstant = Instant.parse(stored)
            val cutoffEpochMs = Clock.System.now().toEpochMilliseconds() - RECOVERY_HOURS_MS
            if (storedInstant.toEpochMilliseconds() < cutoffEpochMs) fallback else stored
        } catch (_: Exception) {
            fallback
        }
    }

    /**
     * Persists current time minus 1 minute (API format) after a successful sync.
     * No arguments; call on success only.
     */
    fun setDemandsLastSync() {
        val epochMs = Clock.System.now().toEpochMilliseconds() - SET_BUFFER_MS
        val value = Instant.fromEpochMilliseconds(epochMs).toApiTimestampString()
        settings.putString(KEY_LAST_DEMAND_SYNC, value)
    }

    private fun fallbackTimestamp(): String {
        val epochMs = Clock.System.now().toEpochMilliseconds() - RECOVERY_HOURS_MS
        return Instant.fromEpochMilliseconds(epochMs).toApiTimestampString()
    }

    private fun Instant.toApiTimestampString(): String =
        this.toLocalDateTime(TimeZone.UTC).toString() + "Z"
}
