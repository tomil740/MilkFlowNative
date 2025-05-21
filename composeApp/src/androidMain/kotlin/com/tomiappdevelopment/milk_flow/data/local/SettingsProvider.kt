package com.tomiappdevelopment.milk_flow.data.local

// androidMain
import android.annotation.SuppressLint
import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

@SuppressLint("StaticFieldLeak")
actual object SettingsProvider {
    private lateinit var context: Context

    fun init(appContext: Context) {
        context = appContext
    }

    actual val settings: Settings by lazy {
        SharedPreferencesSettings(
            context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        )
    }
}
