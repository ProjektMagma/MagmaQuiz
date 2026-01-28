package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.navigation.NavStackHolder.settingsBackStack
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.SettingsScreen

@Composable
fun SettingsNavigation(
    navigateToAuth: () -> Unit
) {

    NavDisplay(
        backStack = settingsBackStack,
        entryDecorators = listOf(
            rememberViewModelStoreNavEntryDecorator(),
            rememberSaveableStateHolderNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Menus.Settings.Profile> {
                SettingsScreen(
                    navigateToAuth = { navigateToAuth() },
                )
            }
        }
    )
}