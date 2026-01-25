package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.projektmagma.magmaquiz.app.core.MainWindow

@Composable
actual fun CustomWindowDraggableArea() {
    MainWindow.frameWindowScope.WindowDraggableArea {
        WindowActionsButtonsRow(modifier = Modifier.fillMaxWidth())
    }
}