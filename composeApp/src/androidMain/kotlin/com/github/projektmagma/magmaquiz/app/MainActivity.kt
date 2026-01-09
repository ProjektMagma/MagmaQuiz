package com.github.projektmagma.magmaquiz.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.projektmagma.magmaquiz.app.core.presentation.App
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        FileKit.init(this)

        setContent {
            App()
        }
    }
}