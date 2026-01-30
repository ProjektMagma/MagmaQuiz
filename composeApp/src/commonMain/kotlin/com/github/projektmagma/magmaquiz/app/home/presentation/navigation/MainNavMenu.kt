package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.runtime.Composable
import com.github.projektmagma.magmaquiz.app.core.di.Navigator

@Composable
expect fun MainNavMenu(
    navigator: Navigator,
    navigateToHome: () -> Unit,
    navigateToQuizzes: () -> Unit,
    navigateToUsers: () -> Unit,
    navigateToUserProfile: () -> Unit,
    content: @Composable () -> Unit
)