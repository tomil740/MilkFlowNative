package com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.core.SyncStatus
import com.tomiappdevelopment.milk_flow.domain.core.allCategories
import com.tomiappdevelopment.milk_flow.domain.models.Category

@Composable
fun StatusMenuBar(
    currentStatus: Status,
    syncStatus: SyncStatus,
    onStatusChange: (Status) -> Unit,
    onSyncClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = colorScheme.surfaceVariant
    val shadowElevation = 4.dp
    val buttonShape = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(shadowElevation, RoundedCornerShape(12.dp))
            .background(containerColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        // ** Top Row: Sync Button + Title + Sync Badge **
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sync button with optional loading spinner
            Box {
                IconButton(
                    onClick = onSyncClicked,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync demands",
                        tint = colorScheme.primary
                    )
                }

                if (syncStatus == SyncStatus.IN_PROGRESS) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center),
                        strokeWidth = 2.dp,
                        color = colorScheme.primary
                    )
                }
            }

            // Title and badge aligned on right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "סטטוס",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                SyncStatusBadge(syncStatus)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status buttons row (unchanged)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Status.values().forEach { status ->
                val isSelected = status == currentStatus
                val backgroundColor = if (isSelected) colorScheme.primary else colorScheme.surface
                val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1f,
                    label = "scaleAnim"
                )

                Button(
                    onClick = { onStatusChange(status) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = backgroundColor,
                        contentColor = contentColor
                    ),
                    shape = buttonShape,
                    border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                    modifier = Modifier
                        .weight(1f)
                        .scale(scale)
                        .height(48.dp)
                ) {
                    Text(
                        text = status.label(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                        )
                }
            }
        }
    }
}


@Composable
fun statusIcon(status: Status): ImageVector = when (status) {
    Status.pending -> Icons.Default.CheckCircle
    Status.placed -> Icons.Default.CheckCircle
    Status.completed -> Icons.Default.Check
}

fun Status.label(): String = when (this) {
    Status.pending -> "ממתין"
    Status.placed -> "שודר"
    Status.completed -> "סופק"
}
