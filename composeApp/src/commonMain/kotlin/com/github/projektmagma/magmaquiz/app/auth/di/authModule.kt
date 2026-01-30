package com.github.projektmagma.magmaquiz.app.auth.di

import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.auth.data.AuthService
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.auth.presentation.screens.LoginScreen
import com.github.projektmagma.magmaquiz.app.auth.presentation.screens.OnBoardingScreen
import com.github.projektmagma.magmaquiz.app.auth.presentation.screens.RegisterScreen
import com.github.projektmagma.magmaquiz.app.core.di.Navigator
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.core.presentation.screens.ServerConfigScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val authModule = module {
    singleOf(::AuthService)
    singleOf(::AuthRepository)
    viewModelOf(::AuthViewModel)
    
    navigation<Route.Auth.Login> {
        val navigator = get<Navigator>()
        LoginScreen(
            navigateToRegister = { navigator.goTo(Route.Auth.Register) },
            navigateToHome = { navigator.goTo(Route.Menus.Home )}
        )
    }
    navigation<Route.Auth.Register> {
        val navigator = get<Navigator>()
        RegisterScreen(
            navigateToLogin = { navigator.goTo(Route.Auth.Login) },
            navigateToHome = { navigator.goTo(Route.Menus.Home )}
        )
    }
    navigation<Route.Auth.OnBoarding> {
        val navigator = get<Navigator>()
        OnBoardingScreen(
            navigateToLogin = { navigator.goTo(Route.Auth.Login) },
            navigateToRegister = { navigator.goTo(Route.Auth.Register) },
            navigateToServerConfig = { navigator.goTo(Route.Auth.ServerConfig) }
        )
    }
    navigation<Route.Auth.ServerConfig> {
        val navigator = get<Navigator>()
        ServerConfigScreen(
            navigateBack = { navigator.goBack() }
        )
    }
}