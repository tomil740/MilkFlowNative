package com.tomiappdevelopment.milk_flow.presentation.productCatalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.presentation.core.NumberWheelPicker

@Composable
fun ProductDialog(
    product: Product,
    onClose: () -> Unit,
    addToCart: (productId: Int, amount: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var amount by remember { mutableIntStateOf(1) }

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = product.name,
                    maxLines=1,
                    overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "קטגורית מוצר: ${product.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "בחר כמות (באריזות)",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Wheel picker using KMP datetimewheelpicker
                NumberWheelPicker(
                    rowCount = 3,
                    start = 1,
                    endInclusive = 300,
                    //textStyle = MaterialTheme.typography.titleMedium,
                    selectedNumber = amount,
                    onValueChange = { amount = it }
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "${product.itemsPerPackage} יחידות בכל אריזה",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "$amount אריזות (${amount * product.itemsPerPackage})",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        addToCart(product.id, amount)
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth(0.85f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("הוסף לסל")
                }
            }

            // Close button top right
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "סגור")
            }
        }
    }
}
