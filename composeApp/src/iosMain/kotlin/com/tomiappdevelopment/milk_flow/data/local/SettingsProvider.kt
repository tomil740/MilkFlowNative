package com.tomiappdevelopment.milk_flow.data.local

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

actual object SettingsProvider {
    actual val settings: Settings = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}
