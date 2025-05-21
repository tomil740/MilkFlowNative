package com.tomiappdevelopment.milk_flow.presentation.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun AuthActionButton(
    userName: String?,
    onLogout: () -> Unit
) {
    var showLogout by remember { mutableStateOf(false) }

    LaunchedEffect(showLogout) {
        if (showLogout) {
            delay(3000L)
            showLogout = false
        }
    }

    val icon = if (showLogout) "🚪" else "👤"
    val label = if (showLogout) "התנתקות" else userName ?: "משתמש"

    ActionButton(
        icon = icon,
        label = label,
        onClick = {
            if (showLogout) {
                onLogout()
            } else {
                showLogout = true
            }
        }
    )
}
