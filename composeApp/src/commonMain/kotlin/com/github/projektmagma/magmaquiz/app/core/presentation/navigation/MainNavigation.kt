package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.github.projektmagma.magmaquiz.app.core.presentation.screens.AboutScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.HomeScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.RoomListScreen
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens.CreateQuestionScreen
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens.CreateQuizScreen
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens.QuizDetailsScreen
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens.QuizReviewsScreen
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens.QuizzesScreen
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.SettingsScreen
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.account.AccountDetailsChangeScreen
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.email.change.EmailChangeScreen
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.email.verify.EmailVerificationScreen
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.location.LocationDetailsChangeScreen
import com.github.projektmagma.magmaquiz.app.users.presentation.screens.UserDetailsScreen
import com.github.projektmagma.magmaquiz.app.users.presentation.screens.UsersScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavigation(
    navigator: Navigator,
    navigationState: NavigationState,
    navigateToAuth: () -> Unit,
    navigateToGameScreen: (route: Route) -> Unit,
    navigateToChangePassword: () -> Unit
) {
    val createQuizViewModel: CreateQuizViewModel = koinViewModel()

    NavDisplay(
        modifier = Modifier.fillMaxWidth(),
        onBack = navigator::goBack,
        entries = navigationState.toEntries(
            entryProvider {
                entry<Route.Menus.Home.Main> {
                    HomeScreen(
                        navigateToQuizDetails = { id ->
                            navigator.navigate(Route.Menus.Quizzes.QuizDetails(id))
                        },
                        navigateToUserDetails = {
                            navigator.navigate(Route.Menus.Users.UserDetails(it))
                        },
                        navigateToQuizReviews = { id, reviewed ->
                            navigator.navigate(Route.Menus.Quizzes.QuizReviews(id, reviewed))
                        },
                        navigateToRoomList = { navigator.navigate(Route.Menus.Home.Rooms) },
                    )
                }
                entry<Route.Menus.Home.Rooms> {
                    RoomListScreen(
                        onJoinClick = {
                            navigateToGameScreen(Route.Game.Wait)
                        }
                    )
                }
                entry<Route.Menus.Settings.Options> {
                    SettingsScreen(
                        navigateToAuth = { navigateToAuth() },
                        navigateToChangeAccountDetailsScreen = { navigator.navigate(Route.Menus.Settings.DetailsChange) },
                        navigateToChangeLocationDetailsScreen = { navigator.navigate(Route.Menus.Settings.LocationChange) },
                        navigateToChangePasswordScreen = { navigateToChangePassword() },
                        navigateToChangeEmailScreen = { navigator.navigate(Route.Menus.Settings.EmailChange) },
                        navigateToAboutScreen = { navigator.navigate(Route.Menus.Settings.About) },
                    )
                }
                entry<Route.Menus.Settings.LocationChange> {
                    LocationDetailsChangeScreen(
                        navigateBack = { navigator.goBack() }
                    )
                }
                entry<Route.Menus.Settings.DetailsChange> {
                    AccountDetailsChangeScreen(
                        goBack = { navigator.goBack() }
                    )
                }
                entry<Route.Menus.Settings.EmailChange> {
                    EmailChangeScreen(
                        navigateToEmailVerification = { navigator.navigate(Route.Menus.Settings.EmailVerification(it)) }
                    )
                }
                entry<Route.Menus.Settings.About> {
                    AboutScreen(
                        navigateBack = { navigator.goBack() }
                    )
                }
                entry<Route.Menus.Settings.EmailVerification> {
                    EmailVerificationScreen(
                        email = it.email,
                        navigateBack = {
                            navigator.goBack()
                            navigator.goBack()
                        },
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
                        navigateToQuizReviews = { id, reviewed ->
                            navigator.navigate(
                                Route.Menus.Quizzes.QuizReviews(
                                    id,
                                    reviewed
                                )
                            )
                        },
                    )
                }
                entry<Route.Menus.Quizzes.QuizDetails> {
                    QuizDetailsScreen(
                        id = it.id,
                        navigateToPlayScreen = {
                            navigateToGameScreen(Route.Game.Singleplayer)
                        },
                        navigateToReviewsScreen = { id, reviewed ->
                            navigator.navigate(Route.Menus.Quizzes.QuizReviews(id, reviewed))
                        },
                        navigateToSettingsScreen = { navigateToGameScreen(Route.Game.Settings) }
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
                entry<Route.Menus.Quizzes.QuizReviews> { parameters ->
                    QuizReviewsScreen(
                        parameters.id,
                        parameters.reviewed,
                        navigateToQuizDetails = {
                            navigator.navigate(Route.Menus.Users.UserDetails(it))
                        }
                    )
                }
                entry<Route.Menus.Users.UserDetails> { parameters ->
                    UserDetailsScreen(
                        id = parameters.id,
                        createQuizViewModel = createQuizViewModel,
                        navigateToEditScreen = { navigator.navigate(Route.Menus.Quizzes.CreateQuiz) },
                        navigateToQuizDetails = { navigator.navigate(Route.Menus.Quizzes.QuizDetails(it)) },
                        navigateToSettingsScreen = { navigator.navigate(Route.Menus.Settings.Options) },
                        navigateToQuizReviews = { id, reviewed ->
                            navigator.navigate(
                                Route.Menus.Quizzes.QuizReviews(
                                    id,
                                    reviewed
                                )
                            )
                        }
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