package com.github.projektmagma.magmaquiz.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.projektmagma.magmaquiz.app.core.di.initKoin
import com.github.projektmagma.magmaquiz.app.core.presentation.App
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.app_icon
import org.jetbrains.compose.resources.painterResource

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            alwaysOnTop = System.getenv("ALWAYS_ON_TOP").toBoolean(),
            title = "Magma Quiz",
            icon = painterResource(Res.drawable.app_icon)
        ) {
            App()
        }   
    }
}