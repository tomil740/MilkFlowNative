package com.tomiappdevelopment.milk_flow.presentation.productCatalog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.presentation.core.components.AsyncImageWithFallback

@Composable
fun ProductPreviewItem(
    product: Product,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(0.75f) // ~16:9 image + name + button
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
            , contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            AsyncImageWithFallback(
                imageUrl = product.effectiveImageUrl(),
                contentDescription = product.name,
                modifier = modifier
                    .padding(16.dp, 16.dp, 16.dp, 0.dp)
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = product.name,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth()
            )
            }

            Spacer(modifier = Modifier.weight(1f)) // Push button to bottom

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onAddToCartClick,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(
                         horizontal = 16.dp,
                      //   vertical = MaterialTheme.typography.bodySmall.fontSize.value.dp * 0.5f
                    ),
                    modifier = Modifier.heightIn(min = MaterialTheme.typography.bodySmall.fontSize.value.dp * 4.2f)
                ) {
                    Text(
                        lineHeight = MaterialTheme.typography.bodySmall.fontSize,
                        textAlign = TextAlign.Center,
                        text = "הוסף",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}