package com.github.projektmagma.magmaquiz.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.projektmagma.magmaquiz.app.core.presentation.App
import com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme.MagmaQuizTheme
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        FileKit.init(this)

        setContent {
            MagmaQuizTheme {
                App()
            }
        }
    }
}