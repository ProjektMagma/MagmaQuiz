package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

@Composable
expect fun MainNavMenu(
    backStack: NavBackStack<NavKey>,
    navigateToHome: () -> Unit,
    navigateToQuizzes: () -> Unit,
    navigateToUsers: () -> Unit,
    navigateToSettings: () -> Unit,
    content: @Composable () -> Unit
)