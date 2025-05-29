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
import androidx.compose.ui.zIndex

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
                ,
            contentAlignment = Alignment.Companion.Center
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

            }
            if (floatingLabel != null && floatingLabel > 0) {
                Box(
                    modifier = Modifier.Companion
                        .align(Alignment.Companion.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .size(height = 21.dp, width = 30.dp)
                        .clip(CircleShape).zIndex(100f)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Companion.TopCenter
                ) {
                    Text(
                        text = floatingLabel.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary
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