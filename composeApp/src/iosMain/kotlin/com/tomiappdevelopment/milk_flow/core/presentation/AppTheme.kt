package com.tomiappdevelopment.milk_flow.core.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.tomiappdevelopment.milk_flow.theme.DarkColors
import com.tomiappdevelopment.milk_flow.theme.LightColors


@Composable
actual fun AppTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
){
    MaterialTheme(
        colorScheme = if(darkTheme) DarkColors else LightColors,
        content = content
    )
}