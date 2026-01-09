package com.github.projektmagma.magmaquiz.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.projektmagma.magmaquiz.app.core.di.initKoin
import com.github.projektmagma.magmaquiz.app.core.presentation.App
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.app_icon
import magmaquiz.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension

fun main() {
    initKoin()
    application {


        Window(
            onCloseRequest = ::exitApplication,
            alwaysOnTop = System.getenv("ALWAYS_ON_TOP").toBoolean(),
            title = stringResource(Res.string.app_name),
            icon = painterResource(Res.drawable.app_icon),
        ) {
            window.minimumSize = Dimension(750, 750)
            App()
        }
    }
}