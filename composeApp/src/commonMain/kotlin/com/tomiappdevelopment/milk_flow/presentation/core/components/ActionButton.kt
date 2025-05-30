package com.tomiappdevelopment.milk_flow.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActionButton(
    icon: String,
    label: String,
    floatingLabel: Int? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        modifier = Modifier.Companion
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.Companion
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Companion.Center
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )

            if (floatingLabel != null && floatingLabel > 0) {
                Box(
                    modifier = Modifier.Companion
                        .align(Alignment.Companion.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Text(
                        text = floatingLabel.toString(),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.Companion.padding(top = 4.dp),
            textAlign = TextAlign.Companion.Center
        )
    }
}