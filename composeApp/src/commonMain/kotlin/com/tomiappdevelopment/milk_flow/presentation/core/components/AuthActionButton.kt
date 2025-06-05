package com.tomiappdevelopment.milk_flow.presentation.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AuthActionButton(
    userName: String?,
    onClick: () -> Unit= {},
    isStatic: Boolean = false,
    amount: Int = 0
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

    Box {

        ActionButton(
            icon = icon,
            label = label,
            onClick = {
                if (!isStatic) {
                    if (showLogout) {
                        onClick()
                    } else {
                        showLogout = true
                    }
                }
            }
        )
        if (amount > 0) {
            Badge(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = (8).dp)
            ) {
                Text(
                    text = amount.toString(),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}


