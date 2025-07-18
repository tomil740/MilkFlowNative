package com.tomiappdevelopment.milk_flow.presentation.CartScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tomiappdevelopment.milk_flow.domain.models.CartProduct
import com.tomiappdevelopment.milk_flow.presentation.core.components.AsyncImageWithFallback

@Composable
fun CartPreviewItem(
    cartProduct: CartProduct,
    onEdit: (CartProduct) -> Unit,
    isDemandItem: Boolean = false,
    modifier: Modifier = Modifier
) {
    val product = cartProduct.product

    Box(
        modifier = modifier
            .clickable { onEdit(cartProduct) }
            .fillMaxWidth()
            .background(
                color =if(isDemandItem){MaterialTheme.colorScheme.surface}else{ MaterialTheme.colorScheme.surfaceVariant},
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        // Edit button (top right corner)
        if(!isDemandItem) {
            IconButton(
                onClick = { onEdit(cartProduct) },
                modifier = Modifier
                    .offset(x = -12.dp, y = -12.dp)
                    .align(Alignment.TopStart)
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .zIndex(10f)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit cart item",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(9.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Product image with fallback
            AsyncImageWithFallback(
                imageUrl = product.effectiveImageUrl(),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Product name & amount
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "כמות: ${cartProduct.amount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
