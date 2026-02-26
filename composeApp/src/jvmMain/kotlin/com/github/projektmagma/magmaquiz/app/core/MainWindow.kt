package com.github.projektmagma.magmaquiz.app.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import java.awt.Dimension
import java.awt.GraphicsEnvironment

object MainWindow {
    lateinit var frameWindowScope: FrameWindowScope
        private set
    lateinit var applicationScope: ApplicationScope
        private set
    lateinit var windowState: WindowState
        private set

    private var _lastPosition: WindowPosition? = null

    private var _lastSize: DpSize? = null

    var isMaximized by mutableStateOf(false)
        private set

    fun setWindowState(
        windowState: WindowState,
        frameWindowScope: FrameWindowScope,
        applicationScope: ApplicationScope,
    ) {
        this.windowState = windowState
        this.frameWindowScope = frameWindowScope
        this.applicationScope = applicationScope
        this.frameWindowScope.window.minimumSize = Dimension(1200, 750)
    }

    fun toggleMaximized() {
        isMaximized = !isMaximized

        if (isMaximized) {
            _lastPosition = windowState.position
            _lastSize = windowState.size
            windowState.position = WindowPosition(0.dp, 0.dp)
            windowState.size = DpSize(
                GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.size.width.dp,
                GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.size.height.dp
            )
        } else {
            windowState.size = _lastSize!!
            windowState.position = _lastPosition!!
        }
    }
}

