package com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.core.getStringName
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import com.tomiappdevelopment.milk_flow.domain.util.toReadableString
import com.tomiappdevelopment.milk_flow.presentation.core.components.AuthActionButton
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun DemandPreviewItem(
    demand: DemandWithNames,
    isDistributer: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDemandDel:()-> Unit = {}
) {

    var showDeleteDialog by remember { mutableStateOf(false) }

    val nowInstant = Clock.System.now()
    val createdAtInstant = demand.createdAt.toInstant(TimeZone.currentSystemDefault())

    val isOldDemand = (demand.status != Status.completed) && (nowInstant - createdAtInstant).inWholeHours > 24

    val containerColor = if (isOldDemand) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val innerContainerColor = if (isOldDemand) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val onSurface = MaterialTheme.colorScheme.onSurface
    val outline = MaterialTheme.colorScheme.outline

    val dateContainerColor =if (isOldDemand) { MaterialTheme.colorScheme.errorContainer
    }else{innerContainerColor}
    val dateColor = if (isOldDemand) { MaterialTheme.colorScheme.error
    }else{onSurface}

    Box(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.zIndex(100f)
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .clickable { onClick() }
    ) {


        // Top Static Auth-Like Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val matchedName: String =
                (if (isDistributer) demand.userName else demand.distributerName) ?: ""
            AuthActionButton(userName = matchedName, isStatic = true)
        }


        // Sub Info Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(innerContainerColor)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Status Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "סטטוס:",
                    style = MaterialTheme.typography.labelSmall,
                    color = onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = demand.status.getStringName(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
            }

            // Products Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "סך מוצרים:",
                    style = MaterialTheme.typography.labelMedium,
                    color = onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = demand.products.size.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
            }

            // Updated At Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.background(dateContainerColor)
            ) {
                Text(
                    text = "עודכן לאחרונה:",
                    style = MaterialTheme.typography.labelMedium,
                    color = dateColor.copy(alpha = 0.7f)
                )
                Text(
                    text = demand.updatedAt.toReadableString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = dateColor
                )
            }
        }

    }
    }
    ConfirmDeleteDialog(
        show = showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        onConfirm = {
            showDeleteDialog = false
            onDemandDel()
        }
    )
}
