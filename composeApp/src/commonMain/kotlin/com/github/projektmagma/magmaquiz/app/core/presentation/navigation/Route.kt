package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

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
        data object Settings : Route {
            @Serializable
            data object Edit : Route
            @Serializable
            data object Profile : Route
            @Serializable data class EditQuestion(val isMultiple: Boolean): Route
        }

        @Serializable
        data object Play : Route {
            @Serializable
            data object Singleplayer : Route

            @Serializable
            data object Multiplayer : Route
        }

        @Serializable
        data object Quizzes : Route {
            @Serializable
            data object Find : Route

            @Serializable
            data class Details(@Serializable(UUIDSerializer::class) val id: UUID) : Route
            
            @Serializable
            data object Game : Route
            
            @Serializable
            data object CreateQuiz: Route
            
            @Serializable
            data class CreateQuestion(val isMultiple: Boolean): Route
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