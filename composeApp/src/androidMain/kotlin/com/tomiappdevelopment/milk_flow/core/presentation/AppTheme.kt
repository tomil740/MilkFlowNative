package com.tomiappdevelopment.milk_flow.core.presentation


import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import com.tomiappdevelopment.milk_flow.theme.DarkColors
import com.tomiappdevelopment.milk_flow.theme.LightColors

@Composable
actual fun AppTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
){
    val context = LocalContext.current
    val colors = when {
        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.primary.toArgb()
            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightStatusBars = darkTheme
        }
    }
    val current = LocalDensity.current
    Log.i("fontScale 1",current.toString())

    val clampedFontScale = current.fontScale.coerceIn(1f, 1.3f) // ✅ Set safe bounds

    CompositionLocalProvider(
        LocalDensity provides Density(current.density, clampedFontScale)
    ) {
        val fontScale = LocalDensity.current.fontScale
        Log.i("fontScale 2",fontScale.toString())

        MaterialTheme(
            colorScheme = colors,
            content = content,
        )
    }
}