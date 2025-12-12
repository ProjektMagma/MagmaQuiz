package com.github.projektmagma.magmaquiz.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.github.projektmagma.magmaquiz.presentation.navigation.NavRoot
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        NavRoot()
    }
}