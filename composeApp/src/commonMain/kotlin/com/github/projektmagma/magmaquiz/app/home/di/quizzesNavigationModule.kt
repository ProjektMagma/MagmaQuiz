package com.github.projektmagma.magmaquiz.app.home.di

import com.github.projektmagma.magmaquiz.app.core.di.Navigator
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.CreateQuestionScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.CreateQuizScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.QuizDetailsScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.QuizzesScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val quizzesNavigationModule = module {
    navigation<Route.Menus.Quizzes.Find> {
        val navigator: Navigator = get()
        QuizzesScreen(
            navigateToQuizDetails = { navigator.goTo(Route.Menus.Quizzes.QuizDetails(it)) },
            navigateToUserDetails = { navigator.goTo(Route.Menus.Users.UserDetails(it)) },
            quizzesListViewModel = koinViewModel()
        )
    }
    navigation<Route.Menus.Quizzes.QuizDetails> {
        val navigator: Navigator = get()
        QuizDetailsScreen(
            id = it.id,
            navigateToPlayScreen = { navigator.goTo(Route.Game) },
            navigateBack = { navigator.goBack() }
        )
    }
    navigation<Route.Menus.Quizzes.CreateQuiz> {
        val navigator: Navigator = get()
        CreateQuizScreen(
            navigateToQuestionCreate = { navigator.goTo(Route.Menus.Quizzes.CreateQuestion(it)) },
            navigateBack = { navigator.goBack() },
        )
    }
    navigation<Route.Menus.Quizzes.CreateQuestion> {
        val navigator: Navigator = get()
        CreateQuestionScreen(
            isMultiple = it.isMultiple,
            navigateBack = { navigator.goBack() },
        )
    }
}