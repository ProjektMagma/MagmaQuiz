package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun ProfilePictureIcon(imageData: ByteArray) {
    AsyncImage(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(25.dp)),
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .crossfade(true)
            .data(imageData)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}