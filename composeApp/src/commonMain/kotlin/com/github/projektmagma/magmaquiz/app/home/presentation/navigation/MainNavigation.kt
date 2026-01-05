package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.HomeScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.SettingsScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.UsersScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun MainNavigation(
    navigateToAuth: () -> Unit
) {
    val mainBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Main.Home::class, Route.Main.Home.serializer())
                    subclass(Route.Main.Quizzes::class, Route.Main.Quizzes.serializer())
                    subclass(Route.Main.Users::class, Route.Main.Users.serializer())
                    subclass(Route.Main.Settings::class, Route.Main.Settings.serializer())
                }
            }
        },
        Route.Main.Home
    )
    MainNavMenu(
        backStack = mainBackStack,
        navigateToHome = {
            mainBackStack.add(Route.Main.Home)
        },
        navigateToQuizzes = {
            mainBackStack.add(Route.Main.Quizzes)
        },
        navigateToUsers = {
            mainBackStack.add(Route.Main.Users)
        },
        navigateToSettings = {
            mainBackStack.add(Route.Main.Settings)
        },
    ) {
        NavDisplay(
            backStack = mainBackStack,
            onBack = {
                if (mainBackStack.size > 1)
                    mainBackStack.removeLastOrNull()
            },
            entryDecorators = listOf(
                rememberViewModelStoreNavEntryDecorator(),
                rememberSaveableStateHolderNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Route.Main.Home> {
                    HomeScreen()
                }
                entry<Route.Main.Quizzes> {
                    QuizzesNavigation()
                }
                entry<Route.Main.Users> {
                    UsersScreen()
                }
                entry<Route.Main.Settings> {
                    SettingsScreen(navigateToAuth = { navigateToAuth() })
                }
            },
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it })
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
        )
    }
}