package com.tomiappdevelopment.milk_flow.presentation.DemandItem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import com.tomiappdevelopment.milk_flow.domain.util.toReadableString
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components.label
import com.tomiappdevelopment.milk_flow.presentation.core.components.AuthActionButton

@Composable
fun DemandInfo(
    modifier: Modifier = Modifier,
    demand: DemandWithNames,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- Header Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Text
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("סטטוס: ", style = MaterialTheme.typography.titleSmall)
                    Text(
                        demand.status.label(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = getStatusColor(demand.status),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Users Section
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AuthActionButton(userName = demand.distributerName, isStatic = true)
                    AuthActionButton(userName = demand.userName, isStatic = true)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Dates Section ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Row {
                    Text(
                        text = "נוצר בתאריך:  ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = demand.createdAt.toReadableString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row {
                    Text(
                        text = "עודכן לאחרונה:  ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = demand.updatedAt.toReadableString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
@Composable
fun getStatusColor(status: Status): Color {
    return when (status) {
        Status.completed -> colorScheme.tertiary   // e.g., teal or accent tone
        Status.pending -> colorScheme.error        // standard error red
        Status.placed -> colorScheme.primary
        // Add other mappings
    }
}
