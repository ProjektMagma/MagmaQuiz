package com.github.projektmagma.magmaquiz.app.home.di

import com.github.projektmagma.magmaquiz.app.core.di.Navigator
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.UserDetailsScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.UsersScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val usersNavigationModule = module {
    navigation<Route.Menus.Users.UsersList>(slideInAndOutHorizontallyAnimation) {
        val navigator: Navigator = get()
        UsersScreen(
            navigateToUserDetails = { navigator.goTo(Route.Menus.Users.UserDetails(it)) }
        )
    }
    navigation<Route.Menus.Users.UserDetails> { parameters ->
        val navigator: Navigator = get()
        UserDetailsScreen(
            id = parameters.id,
            navigateToEditScreen = { navigator.goTo(Route.Menus.Quizzes.CreateQuiz) },
            navigateToQuizDetails = { navigator.goTo(Route.Menus.Quizzes.QuizDetails(it)) },
            navigateToSettingsScreen = { navigator.goTo(Route.Menus.Settings) }
        )
    }
}