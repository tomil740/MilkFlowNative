package com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct
import com.tomiappdevelopment.milk_flow.domain.models.DemandWithNames
import com.tomiappdevelopment.milk_flow.domain.util.toReadableString
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.components.CartHeader
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.components.CartPreviewItem
import com.tomiappdevelopment.milk_flow.presentation.core.components.CheckoutButton
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components.StatusMenuBar
import com.tomiappdevelopment.milk_flow.presentation.core.components.AuthActionButton
import com.tomiappdevelopment.milk_flow.presentation.core.components.LoadingSpinner
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.components.ProductDialog
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
fun DemandPreviewItem(
    demand: DemandWithNames,
    isDistributer: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val innerContainerColor = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface
    val outline = MaterialTheme.colorScheme.outline

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
            val matchedName : String= (if (isDistributer) demand.userName else demand.distributerName) ?: ""
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
                    style = MaterialTheme.typography.labelMedium,
                    color = onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = demand.status.label(),
                    style = MaterialTheme.typography.bodyLarge,
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "עודכן לאחרונה:",
                    style = MaterialTheme.typography.labelMedium,
                    color = onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = demand.updatedAt.toReadableString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = onSurface
                )
            }
        }

    }
}
