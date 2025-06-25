package com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.milk_flow.domain.core.ImageDefaults
import com.tomiappdevelopment.milk_flow.domain.models.ProductSummaryItem
import com.tomiappdevelopment.milk_flow.presentation.core.components.AsyncImageWithFallback
import com.tomiappdevelopment.milk_flow.presentation.core.components.AuthActionButton

@Composable
fun ProductSummaryItemView(
    item: ProductSummaryItem,
    modifier: Modifier = Modifier
) {

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // — Top Row: Image + Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Image with fallback
                AsyncImageWithFallback(
                    imageUrl = item.effectiveImageUrl(),
                    contentDescription = item.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Text Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ID: ${item.productId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

        }
        AsyncImageWithFallback(
            imageUrl = "${ImageDefaults.barcodeBaseUrl}${item.barcode}.png",
            contentDescription = "Barcode ${item.barcode}",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f) // ~wide barcode ratio, adjust as needed
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(8.dp))
        )


        Spacer(modifier = Modifier.height(12.dp))

        // — Total Amount
        val totalAmount = item.amountSum
        Text(
            text = "סך הכול כמות: $totalAmount",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier= Modifier.padding(start = 14.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // — User Demand Details
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp,
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(item.usersDemand) { user ->
                    AuthActionButton(
                        userName = user.userName,
                        onClick = {},
                        isStatic = true,
                        amount = user.amount
                    )
                }
            }
        }
    }
}
