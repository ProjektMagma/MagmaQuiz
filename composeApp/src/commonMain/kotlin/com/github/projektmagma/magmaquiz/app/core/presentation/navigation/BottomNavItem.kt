package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.ui.graphics.vector.ImageVector
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.home_nav
import magmaquiz.composeapp.generated.resources.quizzes_nav
import magmaquiz.composeapp.generated.resources.users_nav
import org.jetbrains.compose.resources.StringResource

data class BottomNavItem (
    val title: StringResource,
    val icon: ImageVector
)

val TOP_LEVEL_DESTINATIONS = mapOf(
    Route.Menus.Home to BottomNavItem(
        title = Res.string.home_nav,
        icon = Icons.Default.Home
    ),
    Route.Menus.Quizzes.QuizList to BottomNavItem(
        title = Res.string.quizzes_nav,
        icon = Icons.Default.Quiz
    ),
    Route.Menus.Users.Find to BottomNavItem(
        title = Res.string.users_nav,
        icon = Icons.Default.Groups
    )
)