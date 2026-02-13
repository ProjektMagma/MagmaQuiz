package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun ProfilePictureIcon(imageData: ByteArray?, size: Dp = 50.dp, iconTint: Color = MaterialTheme.colorScheme.onSurface) {
    if (imageData == null)
        Icon(
            modifier = Modifier.size(size),
            imageVector = Icons.Default.AccountCircle,
            tint = iconTint,
            contentDescription = "NoPicture",
        )
    else
        AsyncImage(
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .crossfade(true)
                .data(imageData)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
}