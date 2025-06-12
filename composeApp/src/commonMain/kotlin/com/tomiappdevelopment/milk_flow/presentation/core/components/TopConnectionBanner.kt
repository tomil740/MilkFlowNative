package com.tomiappdevelopment.milk_flow.presentation.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tomiappdevelopment.milk_flow.domain.core.ConnectionState
import kotlinx.coroutines.delay

@Composable
fun TopConnectionBanner(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier.zIndex(100f)
) {
    var visible by remember { mutableStateOf(connectionState != ConnectionState.Available) }

    // Auto-hide when state is Available
    LaunchedEffect(connectionState) {
        if (connectionState == ConnectionState.Available) {
            visible = true
            delay(2000)
            visible = false
        } else {
            visible = true
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -40 }),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .alpha(10f)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            ConnectionIndicator(connectionState = connectionState)
        }
    }
}
