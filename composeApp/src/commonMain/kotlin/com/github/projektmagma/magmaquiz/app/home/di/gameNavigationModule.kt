package com.github.projektmagma.magmaquiz.app.home.di

import com.github.projektmagma.magmaquiz.app.core.di.Navigator
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.GameScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val gameNavigationModule = module { 
    navigation<Route.Game> {
        val navigator: Navigator = get()
        GameScreen(
            navigateOnGameFinish = { navigator.clearAndGoTo(Route.Menus.Home) }
        ) 
    }
}