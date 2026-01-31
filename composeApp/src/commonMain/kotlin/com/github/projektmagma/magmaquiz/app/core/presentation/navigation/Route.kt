package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import java.util.*

sealed interface Route {

    data object Auth : Route {

        data object Login : Route

        data object Register : Route

        data object OnBoarding : Route

        data object ServerConfig : Route
    }

    data object Game : Route {

        data object Singleplayer : Route

        data object Multiplayer : Route
    }

    sealed interface Menus : Route {

        data object Home : Menus

        data object Settings : Menus {

            data object Edit : Menus

            data object Profile : Menus
        }

        sealed interface Quizzes : Menus {

            data object QuizzesList : Quizzes

            data object CreateQuiz : Quizzes

            data class CreateQuestion(val isMultiple: Boolean) : Quizzes

            data class QuizDetails(val id: UUID) : Quizzes
        }

        sealed interface Users : Menus {

            data object UsersList : Users

            data class UserDetails(val id: UUID) : Users

            data object Friends : Users
        }
    }
}