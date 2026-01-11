package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.SubcomposeAsyncImage
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch

@Composable
fun QuizCoverImage(
    height: Dp,
    model: PlatformFile?,
    modifier: Modifier = Modifier,
    onImageClick: ((PlatformFile?) -> Unit)? = null
){
    val scope = rememberCoroutineScope()
    SubcomposeAsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(MaterialTheme.shapes.medium)
            .then(
                if (onImageClick != null) {
                    Modifier
                        .clickable(
                            onClick = {
                                scope.launch {
                                    val image = FileKit.openFilePicker(
                                        type = FileKitType.Image,
                                        mode = FileKitMode.Single
                                    )
                                    onImageClick(image)
                                }
                            },
                        )
                } else { Modifier }
            ).background(MaterialTheme.colorScheme.surfaceContainer),
        model = model,
        error = {
            Icon(
                imageVector = Icons.Default.ImageNotSupported,
                contentDescription = "Nie ma zdjecia"
            )
        },
        contentScale = ContentScale.Crop,
        contentDescription = "Ikona quizu"
    )
}