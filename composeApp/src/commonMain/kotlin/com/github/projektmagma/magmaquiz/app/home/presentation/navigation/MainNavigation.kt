package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.material3.Text
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
                    subclass(Route.Main.Play::class, Route.Main.Play.serializer())
                    subclass(Route.Main.Quizzes::class, Route.Main.Quizzes.serializer())
                    subclass(Route.Main.Users::class, Route.Main.Users.serializer())
                    subclass(Route.Main.Settings::class, Route.Main.Settings.serializer())
                }
            }
        },
        Route.Main.Home
    )
    MainNavMenu(
        navigateToHome = {
            mainBackStack.clear()
            mainBackStack.add(Route.Main.Home)
        },
        navigateToPlay = {
            mainBackStack.clear()
            mainBackStack.add(Route.Main.Play)
        },
        navigateToQuizzes = {
            mainBackStack.clear()
            mainBackStack.add(Route.Main.Quizzes)
        },
        navigateToUsers = {
            mainBackStack.clear()
            mainBackStack.add(Route.Main.Users)
        },
        navigateToSettings = {
            mainBackStack.clear()
            mainBackStack.add(Route.Main.Settings)
        },
    ) {
        NavDisplay(
            backStack = mainBackStack,
            entryDecorators = listOf(
                rememberViewModelStoreNavEntryDecorator(),
                rememberSaveableStateHolderNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Route.Main.Home> {
                    HomeScreen()
                }
                entry<Route.Main.Play> {
                    Text("Play")
                }
                entry<Route.Main.Quizzes> {
                    QuizzesNavigation()
                }
                entry<Route.Main.Users> {
                    Text("Users")
                }
                entry<Route.Main.Settings> {
                    SettingsScreen(navigateToAuth = { navigateToAuth() })
                }
            }
        )
    }
}