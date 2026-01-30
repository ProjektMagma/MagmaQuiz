package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed interface Route {
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
    data object Game : Route {
        @Serializable
        data object Singleplayer : Route

        @Serializable
        data object Multiplayer : Route
    }

    @Serializable
    sealed interface Menus : Route {
        @Serializable
        data object Home : Menus
        
        @Serializable
        data object Settings : Menus {
            @Serializable
            data object Edit : Menus

            @Serializable
            data object Profile : Menus
        }

        @Serializable
        sealed interface Quizzes : Menus {
            @Serializable
            data object Find : Quizzes

            @Serializable
            data object CreateQuiz : Quizzes

            @Serializable
            data class CreateQuestion(val isMultiple: Boolean) : Quizzes

            @Serializable
            data class QuizDetails(@Serializable(UUIDSerializer::class) val id: UUID) : Quizzes
        }

        @Serializable
        sealed interface Users : Menus {
            @Serializable
            data object Find : Users

            @Serializable
            data class UserDetails(@Serializable(UUIDSerializer::class) val id: UUID): Users

            @Serializable
            data object Friends : Users
        }
    }
}