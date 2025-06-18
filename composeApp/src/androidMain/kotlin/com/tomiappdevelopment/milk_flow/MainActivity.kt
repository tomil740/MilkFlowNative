package com.tomiappdevelopment.milk_flow

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.tomiappdevelopment.milk_flow.core.notifications.NotificationPermissionHelper
import com.tomiappdevelopment.milk_flow.core.presentation.NotificationPermissionRequester
import com.tomiappdevelopment.milk_flow.data.local.SettingsProvider

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsProvider.init(this)

        setContent {
            val permissionHelper = remember { NotificationPermissionHelper(this) }

            NotificationPermissionRequester(
                activity = this,
                permissionHelper = permissionHelper,
                onPermissionGranted = {}
            )
            App(isSystemInDarkTheme(),true)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(isSystemInDarkTheme(),true)
}