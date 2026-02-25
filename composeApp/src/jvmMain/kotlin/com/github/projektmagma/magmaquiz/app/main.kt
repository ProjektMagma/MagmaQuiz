package com.github.projektmagma.magmaquiz.app

import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.projektmagma.magmaquiz.app.core.MainWindow
import com.github.projektmagma.magmaquiz.app.core.di.initKoin
import com.github.projektmagma.magmaquiz.app.core.presentation.App
import com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme.MagmaQuizTheme
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.app_icon
import magmaquiz.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() {
    initKoin()
    application {
        val windowState = rememberWindowState()
        Window(
            onCloseRequest = ::exitApplication,
            alwaysOnTop = System.getenv("ALWAYS_ON_TOP").toBoolean(),
            title = stringResource(Res.string.app_name),
            icon = painterResource(Res.drawable.app_icon),
            state = windowState,
            undecorated = true,
            transparent = true
        ) {
            MainWindow.setWindowState(windowState, this, this@application)
            MagmaQuizTheme {
                App(
                    modifier = Modifier
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primaryContainer,
                            if (!MainWindow.isMaximized)
                                MaterialTheme.shapes.large
                            else RectangleShape
                        )
                        .clip(
                            if (!MainWindow.isMaximized)
                                MaterialTheme.shapes.large
                            else RectangleShape
                        )
                )
            }
        }
    }
}