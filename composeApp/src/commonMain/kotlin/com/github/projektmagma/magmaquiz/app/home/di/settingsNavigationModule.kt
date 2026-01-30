package com.github.projektmagma.magmaquiz.app.home.di

import com.github.projektmagma.magmaquiz.app.core.di.Navigator
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.SettingsScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val settingsNavigationModule = module { 
    navigation<Route.Menus.Settings> {
        val navigator: Navigator = get()
        SettingsScreen(
            navigateToAuth = { navigator.clearAndGoTo(Route.Auth.OnBoarding) }
        )
    }
}