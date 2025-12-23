package com.github.projektmagma.magmaquiz.presentation

import androidx.compose.runtime.Composable
import com.github.projektmagma.magmaquiz.presentation.navigation.NavRoot
import com.github.projektmagma.magmaquiz.presentation.ui.theme.MagmaQuizTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MagmaQuizTheme {
        NavRoot()
    }
}