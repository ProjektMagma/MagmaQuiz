package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import com.github.projektmagma.magmaquiz.app.core.MainWindow

@Composable
fun WindowActionsButtonsRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.End,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        val colorHover = IconButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            disabledContainerColor = MaterialTheme.colorScheme.error,
            disabledContentColor = MaterialTheme.colorScheme.onError
        )
        val mutableInteractionSource = remember { MutableInteractionSource() }
        val isHovered by mutableInteractionSource.collectIsHoveredAsState()

        IconButton(
            modifier = Modifier.size(50.dp).padding(0.dp),
            shape = RectangleShape,
            onClick = {
                MainWindow.windowState.isMinimized = !MainWindow.windowState.isMinimized
            }) {
            Icon(
                imageVector = Icons.Default.Minimize,
                contentDescription = null
            )
        }


        IconButton(
            modifier = Modifier.size(50.dp).padding(0.dp),
            shape = RectangleShape,
            onClick = {
                MainWindow.windowState.placement =
                    if (MainWindow.windowState.placement == WindowPlacement.Maximized)
                        WindowPlacement.Floating else WindowPlacement.Maximized
            }) {
            Icon(
                imageVector = if (MainWindow.windowState.placement == WindowPlacement.Maximized) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                contentDescription = null
            )
        }

        IconButton(
            modifier = Modifier.size(50.dp).padding(0.dp),
            shape = RectangleShape,
            colors = if (isHovered) colorHover else IconButtonDefaults.iconButtonColors(),
            interactionSource = mutableInteractionSource,
            onClick = {
                MainWindow.applicationScope.exitApplication()
            }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
        }
    }
}