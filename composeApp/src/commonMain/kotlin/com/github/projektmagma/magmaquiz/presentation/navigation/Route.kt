package com.github.projektmagma.magmaquiz.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Auth: Route {
        @Serializable
        data object Login : Route
        
        @Serializable
        data object Register : Route
    }
}