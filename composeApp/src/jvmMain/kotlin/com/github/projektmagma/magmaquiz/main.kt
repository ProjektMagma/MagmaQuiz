package com.github.projektmagma.magmaquiz

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.projektmagma.magmaquiz.di.initKoin
import com.github.projektmagma.magmaquiz.presentation.App

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "magmaquiz",
        ) {
            App()
        }   
    }
}