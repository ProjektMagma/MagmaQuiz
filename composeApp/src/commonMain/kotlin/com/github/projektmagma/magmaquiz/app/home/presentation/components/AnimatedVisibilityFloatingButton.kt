package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedVisibilityFloatingButton(
    isShown: Boolean,
    animationPositionMultiplier: Int = 2,
    onClick: () -> Unit,
    imageVector: ImageVector = Icons.Default.Add
) {
    AnimatedVisibility(
        visible = isShown,
        enter = slideInVertically { height -> height * animationPositionMultiplier },
        exit = slideOutVertically { height -> height * animationPositionMultiplier }
    ) {
        IconButton(
            modifier = Modifier.size(50.dp),
            colors = IconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            onClick = {
                onClick()
            }) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
            )
        }
    }
}