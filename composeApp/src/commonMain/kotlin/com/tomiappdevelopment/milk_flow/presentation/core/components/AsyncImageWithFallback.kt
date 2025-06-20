package com.tomiappdevelopment.milk_flow.presentation.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.tomiappdevelopment.milk_flow.domain.core.ImageDefaults

@Composable
fun AsyncImageWithFallback(
    imageUrl: String,
    fallbackUrl: String = ImageDefaults.fallbackImageUrl,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    var currentUrl by remember { mutableStateOf(imageUrl) }

    AsyncImage(
        model = currentUrl,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        onState = {
            if (it is AsyncImagePainter.State.Error && currentUrl != fallbackUrl) {
                currentUrl = fallbackUrl
            }
        }
    )
}
