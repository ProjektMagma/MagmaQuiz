package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.HomeScreen
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens.CreateQuestionScreen
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens.CreateQuizScreen
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens.QuizDetailsScreen
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens.QuizzesScreen
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.SettingsScreen
import com.github.projektmagma.magmaquiz.app.users.presentation.screens.UserDetailsScreen
import com.github.projektmagma.magmaquiz.app.users.presentation.screens.UsersScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavigation(
    navigator: Navigator,
    navigationState: NavigationState,
    navigateToAuth: () -> Unit,
    navigateToGameScreen: () -> Unit
) {
    val quizzesListViewModel: QuizzesListViewModel = koinViewModel()
    val createQuizViewModel: CreateQuizViewModel = koinViewModel()

    NavDisplay(
        onBack = navigator::goBack,
        entries = navigationState.toEntries(
            entryProvider {
                entry<Route.Menus.Home> {
                    HomeScreen()
                }
                entry<Route.Menus.Settings> {
                    SettingsScreen(
                        navigateToAuth = { navigateToAuth() },
                    )
                }
                entry<Route.Menus.Quizzes.QuizList> {
                    QuizzesScreen(
                        navigateToUserDetails = {
                            navigator.navigate(Route.Menus.Users.UserDetails(it))
                        },
                        navigateToQuizDetails = { id ->
                            navigator.navigate(Route.Menus.Quizzes.QuizDetails(id))
                        },
                        quizzesListViewModel = quizzesListViewModel
                    )
                }
                entry<Route.Menus.Quizzes.QuizDetails> {
                    QuizDetailsScreen(
                        id = it.id,
                        navigateToPlayScreen = {
                            navigateToGameScreen()
                        },
                    )
                }
                entry<Route.Menus.Quizzes.CreateQuiz> {
                    CreateQuizScreen(
                        navigateToQuestionCreate = {
                            navigator.navigate(Route.Menus.Quizzes.CreateQuestion(it))
                        },
                        navigateBack = {
                            navigator.goBack()
                        },
                        createQuizViewModel = createQuizViewModel
                    )
                }
                entry<Route.Menus.Quizzes.CreateQuestion> {
                    CreateQuestionScreen(
                        it.isMultiple,
                        createQuizViewModel = createQuizViewModel,
                        navigateBack = { navigator.goBack() }
                    )
                }
                entry<Route.Menus.Users.UserDetails> { parameters ->
                    UserDetailsScreen(
                        id = parameters.id,
                        createQuizViewModel = createQuizViewModel,
                        navigateToEditScreen = { navigator.navigate(Route.Menus.Quizzes.CreateQuiz) },
                        navigateToQuizDetails = { navigator.navigate(Route.Menus.Quizzes.QuizDetails(it)) },
                        navigateToSettingsScreen = { navigator.navigate(Route.Menus.Settings) }
                    )
                }
                entry<Route.Menus.Users.Find> {
                    UsersScreen(navigateToUserDetails = {
                        navigator.navigate(Route.Menus.Users.UserDetails(it))
                    })
                }
            }
        ),
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