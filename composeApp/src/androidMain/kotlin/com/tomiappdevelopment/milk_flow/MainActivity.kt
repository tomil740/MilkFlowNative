package com.tomiappdevelopment.milk_flow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tomiappdevelopment.milk_flow.data.local.SettingsProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsProvider.init(this)

        setContent {
            App(isSystemInDarkTheme(),true)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(isSystemInDarkTheme(),true)
}