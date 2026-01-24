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
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.HomeScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.koinInject

@Composable
fun MainNavigation(
    navigateToGameScreen: () -> Unit,
    navigateToAuth: () -> Unit
) {
    val authRepository: AuthRepository = koinInject()
    
    val mainBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Menus.Home::class, Route.Menus.Home.serializer())
                    subclass(Route.Menus.Quizzes::class, Route.Menus.Quizzes.serializer())
                    subclass(Route.Menus.Users::class, Route.Menus.Users.serializer())
                }
            }
        },
        Route.Menus.Home
    )
    
    MainNavMenu(
        backStack = mainBackStack,
        navigateToHome = {
            if (mainBackStack.getOrNull(mainBackStack.size - 2) == Route.Menus.Home)
                mainBackStack.removeLastOrNull()
            else
                mainBackStack.add(Route.Menus.Home)
        },
        navigateToQuizzes = {
            if (mainBackStack.getOrNull(mainBackStack.size - 2) == Route.Menus.Quizzes)
                mainBackStack.removeLastOrNull()
            else
                mainBackStack.add(Route.Menus.Quizzes())
        },
        navigateToUsers = {
            if (mainBackStack.getOrNull(mainBackStack.size - 2) == Route.Menus.Users)
                mainBackStack.removeLastOrNull()
            else
                mainBackStack.add(Route.Menus.Users())
        },
        navigateToUserProfile = {
            val route = Route.Menus.Users(Route.Menus.Users.UserDetails(authRepository.thisUser.value?.userId!!))
            if (mainBackStack.getOrNull(mainBackStack.size - 2) == route)
                mainBackStack.removeLastOrNull()
            else
                mainBackStack.add(route)
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
                entry<Route.Menus.Home> {
                    HomeScreen()
                }
                entry<Route.Menus.Quizzes> { parameters ->
                    QuizzesNavigation(
                        startDestination = parameters.route,
                        navigateToUserDetails = { mainBackStack.add(Route.Menus.Users(Route.Menus.Users.UserDetails(it))) },
                        navigateToGameScreen = { navigateToGameScreen() },
                        onSystemBack = { mainBackStack.removeLastOrNull() }
                    )
                }
                entry<Route.Menus.Users> { parameters ->
                    UsersNavigation(
                        startDestination = parameters.route,
                        navigateToQuizDetails = { mainBackStack.add(Route.Menus.Quizzes(Route.Menus.Quizzes.QuizDetails(it))) },
                        navigateToEditScreen = { mainBackStack.add(Route.Menus.Quizzes(Route.Menus.Quizzes.CreateQuiz(it))) },
                        navigateToSettingScreen = { mainBackStack.add(Route.Menus.Settings) }
                    )
                }
                entry<Route.Menus.Settings> { 
                    SettingsNavigation(
                        navigateToAuth = { navigateToAuth() },
                    )
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