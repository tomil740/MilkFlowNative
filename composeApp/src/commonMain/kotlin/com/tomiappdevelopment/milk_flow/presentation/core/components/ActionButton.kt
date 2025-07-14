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
import androidx.compose.ui.layout.HorizontalAlignmentLine
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
    val baseFontSize = MaterialTheme.typography.titleMedium.fontSize
    val iconFontSize = baseFontSize * 2.0f   // Adjust for visual weight
    val badgeFontSize = baseFontSize * 0.7f
    val circleSize = baseFontSize.value.dp * 4.6f  // = ~56.dp at 12.sp
    val badgeHeight = baseFontSize.value.dp * 1.8f // = ~21.dp
    val badgeWidth = baseFontSize.value.dp * 2.5f  // = ~30.dp
    val badgeOffset = baseFontSize.value.dp * 0.5f // ~6.dp offset
    val verticalSpacing = baseFontSize.value.dp * 0.5f // spacing below icon

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(all = baseFontSize.value.dp * 0.7f)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(circleSize),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = iconFontSize
                )
            }

            if (floatingLabel != null && floatingLabel > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = badgeOffset, y = -badgeOffset)
                        .size(width = badgeWidth, height = badgeHeight)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .zIndex(100f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = floatingLabel.toString(),
                        fontSize = badgeFontSize,
                        lineHeight = badgeFontSize, // eliminate excess vertical space
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.zIndex(110f),
                        maxLines = 1
                        )
                }
            }
        }

        Text(
            text = label,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = verticalSpacing),
            textAlign = TextAlign.Center
        )
    }
}
