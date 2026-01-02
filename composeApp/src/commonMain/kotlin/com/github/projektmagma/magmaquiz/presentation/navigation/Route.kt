package com.github.projektmagma.magmaquiz.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Auth : Route {
        @Serializable
        data object Login : Route

        @Serializable
        data object Register : Route

        @Serializable
        data object OnBoarding : Route

        @Serializable
        data object ServerConfig : Route
    }

    @Serializable
    data object Main : Route {
        @Serializable
        data object Home : Route

        @Serializable
        data object Settings : Route

        @Serializable
        data object Play : Route {
            @Serializable
            data object Singleplayer

            @Serializable
            data object Multiplayer
        }

        @Serializable
        data object Quizzes : Route {
            @Serializable
            data object Find : Route

            @Serializable
            data object Favorites : Route
        }

        @Serializable
        data object Users : Route {
            @Serializable
            data object Find : Route

            @Serializable
            data object Friends : Route
        }
    }
}