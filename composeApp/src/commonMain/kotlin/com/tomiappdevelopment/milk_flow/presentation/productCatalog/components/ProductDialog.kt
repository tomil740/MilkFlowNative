package com.tomiappdevelopment.milk_flow.presentation.productCatalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
    onAddOrUpdate: (productId: Int, amount: Int) -> Unit,
    modifier: Modifier = Modifier,
    initialAmount: Int? = null, // null = marketplace mode
) {
    var amount by remember { mutableIntStateOf(initialAmount ?: 1) }

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
                    model = "https://milkflow.netlify.app/productsImages/regular/${product.barcode}.webp",
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
                    maxLines = 1,
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

                NumberWheelPicker(
                    rowCount = 3,
                    start = 1,
                    endInclusive = 300,
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

                if (initialAmount == null) {
                    // Marketplace mode
                    Button(
                        onClick = {
                            onAddOrUpdate(product.id, amount)
                            onClose()
                        },
                        modifier = Modifier.fillMaxWidth(0.85f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("הוסף לסל")
                    }
                } else {
                    // Cart Edit mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = {
                                //-1 flag to delete
                                onAddOrUpdate?.invoke(product.id,-1)
                                onClose()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("הסר מהסל")
                        }

                        Spacer(Modifier.width(16.dp))

                        Button(
                            onClick = {
                                onAddOrUpdate(product.id, amount)
                                onClose()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("עדכן כמות")
                        }
                    }
                }
            }

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
