package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.runtime.Composable
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.NavigationState
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Navigator

@Composable
expect fun MainNavMenu(
    navigator: Navigator,
    navigationState: NavigationState,
    navigateToUserProfile: () -> Unit,
    content: @Composable () -> Unit
)