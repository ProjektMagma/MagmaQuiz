package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

@Composable
expect fun MainNavMenu(
    mainBackStack: NavBackStack<NavKey> = NavStackHolder.mainBackStack,
    quizzesBackstack: NavBackStack<NavKey> = NavStackHolder.quizzesBackstack,
    navigateToHome: () -> Unit,
    navigateToQuizzes: () -> Unit,
    navigateToUsers: () -> Unit,
    navigateToUserProfile: () -> Unit,
    content: @Composable () -> Unit
)