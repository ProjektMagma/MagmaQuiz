package com.github.projektmagma.magmaquiz.presentation.navigation

import androidx.compose.runtime.Composable

@Composable
actual fun MainNavMenu(
    navigateToHome: () -> Unit,
    navigateToPlay: () -> Unit,
    navigateToQuizzes: () -> Unit,
    navigateToUsers: () -> Unit,
    navigateToSettings: () -> Unit,
    content: @Composable (() -> Unit)
) {
}