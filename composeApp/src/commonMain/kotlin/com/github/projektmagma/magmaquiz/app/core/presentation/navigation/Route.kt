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
    data class Game(val startRoute: Route) : Route {
        @Serializable 
        data object Singleplayer : Route
        
        @Serializable
        data object Multiplayer : Route
        
        @Serializable
        data object Wait : Route
        
        @Serializable
        data object Settings : Route
    }

    @Serializable
    data object Menus : Route {
        @Serializable
        data object Home : Route {
            @Serializable
            data object Main : Route
            
            @Serializable
            data object Rooms : Route
        }

        @Serializable
        data object Settings : Route {
            @Serializable
            data object Options : Route

            @Serializable
            data object DetailsChange : Route
            
            @Serializable
            data object LocationChange : Route
            
            @Serializable
            data object EmailChange : Route
            
            @Serializable
            data class EmailVerification(val email: String) : Route
            
            @Serializable
            data object PasswordEmailEntry : Route
            
            @Serializable
            data object PasswordVerification : Route
            
            @Serializable
            data class PasswordChange(val forgot: Boolean) : Route
        }

        @Serializable
        data object Quizzes : Route {
            @Serializable
            data object QuizList : Route

            @Serializable
            data object CreateQuiz : Route

            @Serializable
            data class CreateQuestion(val isMultiple: Boolean) : Route

            @Serializable
            data class QuizDetails(@Serializable(UUIDSerializer::class) val id: UUID) : Route
            
            @Serializable 
            data class QuizReviews(@Serializable(UUIDSerializer::class) val id: UUID, val reviewed: Boolean) : Route
        }

        @Serializable
        data object Users : Route {
            @Serializable
            data object Find : Route

            @Serializable
            data class UserDetails(@Serializable(UUIDSerializer::class) val id: UUID): Route

            @Serializable
            data object Friends : Route
        }
    }
}