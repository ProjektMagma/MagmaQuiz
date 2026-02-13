package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.runtime.Composable

@Composable
expect fun MainNavMenu(
    navigator: Navigator,
    navigationState: NavigationState,
    navigateToUserProfile: () -> Unit,
    content: @Composable () -> Unit
)