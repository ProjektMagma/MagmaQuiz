package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.navigation.NavStackHolder.mainBackStack
import com.github.projektmagma.magmaquiz.app.home.presentation.navigation.NavStackHolder.quizzesBackstack
import com.github.projektmagma.magmaquiz.app.home.presentation.navigation.NavStackHolder.settingsBackStack
import com.github.projektmagma.magmaquiz.app.home.presentation.navigation.NavStackHolder.usersNavBackStack
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.HomeScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavigation(
    navigateToGameScreen: () -> Unit,
    navigateToAuth: () -> Unit
) {
    val authRepository: AuthRepository = koinInject()

    mainBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Menus.Home::class, Route.Menus.Home.serializer())
                    subclass(Route.Menus.Quizzes::class, Route.Menus.Quizzes.serializer())
                    subclass(Route.Menus.Users::class, Route.Menus.Users.serializer())
                    subclass(Route.Menus.Settings::class, Route.Menus.Settings.serializer())
                }
            }
        },
        Route.Menus.Home
    )

    quizzesBackstack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Menus.Quizzes.Find::class, Route.Menus.Quizzes.Find.serializer())
                    subclass(Route.Menus.Quizzes.CreateQuiz::class, Route.Menus.Quizzes.CreateQuiz.serializer())
                    subclass(Route.Menus.Quizzes.CreateQuestion::class, Route.Menus.Quizzes.CreateQuestion.serializer())
                    subclass(Route.Menus.Quizzes.QuizDetails::class, Route.Menus.Quizzes.QuizDetails.serializer())
                }
            }
        },
    )

    usersNavBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Menus.Users.UserDetails::class, Route.Menus.Users.UserDetails.serializer())
                    subclass(Route.Menus.Users.Find::class, Route.Menus.Users.Find.serializer())
                    subclass(Route.Menus.Users.Friends::class, Route.Menus.Users.Friends.serializer())
                }
            }
        },
    )

    settingsBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Menus.Settings.Profile::class, Route.Menus.Settings.Profile.serializer())
                    subclass(Route.Menus.Settings.Edit::class, Route.Menus.Settings.Edit.serializer())
                }
            }
        },
        Route.Menus.Settings.Profile
    )


    val quizzesListViewModel: QuizzesListViewModel = koinViewModel()
    val createQuizViewModel: CreateQuizViewModel = koinViewModel()

    MainNavMenu(
        navigateToHome = {
            if (mainBackStack.getOrNull(mainBackStack.size - 2) == Route.Menus.Home)
                mainBackStack.removeLastOrNull()
            else
                mainBackStack.add(Route.Menus.Home)
        },
        navigateToQuizzes = {
            if (mainBackStack.getOrNull(mainBackStack.size - 2) is Route.Menus.Quizzes)
                mainBackStack.removeLastOrNull()
            else mainBackStack.add(Route.Menus.Quizzes())
        },
        navigateToUsers = {
            if (mainBackStack.getOrNull(mainBackStack.size - 2) is Route.Menus.Users)
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
                    LaunchedEffect(true) {
                        quizzesBackstack.clear()
                        quizzesBackstack.add(parameters.route)
                    }
                    if (quizzesBackstack.isNotEmpty())
                        QuizzesNavigation(
                            navigateToUserDetails = {
                                mainBackStack.add(Route.Menus.Users(Route.Menus.Users.UserDetails(it)))
                            },
                            navigateToGameScreen = {
                                navigateToGameScreen()
                            },
                            onSystemBack = {
                                mainBackStack.removeLastOrNull()
                            },
                            quizzesListViewModel = quizzesListViewModel,
                            createQuizViewModel = createQuizViewModel
                        )
                }
                entry<Route.Menus.Users> { parameters ->
                    LaunchedEffect(true) {
                        usersNavBackStack.clear()
                        usersNavBackStack.add(parameters.route)
                    }
                    if (usersNavBackStack.isNotEmpty())
                        UsersNavigation(
                            navigateToQuizDetails = {
                                mainBackStack.add(
                                    Route.Menus.Quizzes(
                                        Route.Menus.Quizzes.QuizDetails(
                                            it
                                        )
                                    )
                                )
                            },
                            navigateToEditScreen = { mainBackStack.add(Route.Menus.Quizzes(Route.Menus.Quizzes.CreateQuiz)) },
                            navigateToSettingScreen = { mainBackStack.add(Route.Menus.Settings) },
                            createQuizViewModel = createQuizViewModel
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