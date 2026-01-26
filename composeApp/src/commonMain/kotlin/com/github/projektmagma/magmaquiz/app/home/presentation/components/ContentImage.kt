package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun ContentImage(
    imageData: ByteArray?,
    imageSize: Dp = 200.dp,
) {
    if (imageData == null)
        Icon(
            modifier = Modifier.size(imageSize),
            imageVector = Icons.Default.ImageNotSupported,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "NoPicture",
        )
    else
        AsyncImage(
            modifier = Modifier
                .size(imageSize)
                .clip(MaterialTheme.shapes.large),
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .crossfade(true)
                .data(imageData)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
}