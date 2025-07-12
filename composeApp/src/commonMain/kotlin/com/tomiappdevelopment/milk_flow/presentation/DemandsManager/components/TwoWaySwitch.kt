package com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.core.SyncStatus
import com.tomiappdevelopment.milk_flow.domain.core.allCategories
import com.tomiappdevelopment.milk_flow.domain.models.Category

@Composable
fun TwoWaySwitch(
    isProductSummary: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(isProductSummary, label = "SwitchTransition")

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(colorScheme.surface)
            .border(1.dp, colorScheme.outline, RoundedCornerShape(24.dp))
    ) {
        val fullWidth = maxWidth
        val halfWidth = fullWidth / 2

        val indicatorOffset by transition.animateDp(label = "IndicatorOffset") { state ->
            if (state) halfWidth else 0.dp
        }

        // Sliding indicator background
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(halfWidth)
                .fillMaxHeight()
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(24.dp)
                )
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Demand button (left)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = isProductSummary) { onToggle(false) }
                    .zIndex(if (!isProductSummary) 2f else 1f), // ensure text is above background
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📝 סיכום הזמנות",
                    color = if (!isProductSummary) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }

            // Product button (right)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = !isProductSummary) { onToggle(true) }
                    .zIndex(if (isProductSummary) 2f else 1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🛍️ סיכום מוצרים",
                    color = if (isProductSummary) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

