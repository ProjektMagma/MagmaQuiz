package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route.Menus.Quizzes
import com.github.projektmagma.magmaquiz.app.home.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.CreateQuestionScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.CreateQuizScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.QuizDetailsScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.QuizzesScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import java.util.UUID

@Composable
fun QuizzesNavigation(
    startDestination: Route,
    navigateToUserDetails: (id: UUID) -> Unit,
    navigateToGameScreen: () -> Unit,
    onSystemBack: () -> Unit,
    quizzesListViewModel: QuizzesListViewModel,
    createQuizViewModel: CreateQuizViewModel
) {
    val quizzesBackstack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Quizzes.Find::class, Quizzes.Find.serializer())
//                    subclass(Quizzes.Game::class, Quizzes.Game.serializer())
                    subclass(Quizzes.CreateQuiz::class, Quizzes.CreateQuiz.serializer())
                    subclass(Quizzes.CreateQuestion::class, Quizzes.CreateQuestion.serializer())
                    subclass(Quizzes.QuizDetails::class, Quizzes.QuizDetails.serializer())
                }
            }
        },
        startDestination
    )

    fun navigateBack() {
        if (quizzesBackstack.size > 1) {
            quizzesBackstack.removeLastOrNull()
        } else {
            onSystemBack()
        }
    }
    
    NavDisplay(
        backStack = quizzesBackstack,
        entryDecorators = listOf(
            rememberViewModelStoreNavEntryDecorator(),
            rememberSaveableStateHolderNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Quizzes.Find> {
                QuizzesScreen(
                    navigateToCreateQuizScreen = {
                        quizzesBackstack.add(Quizzes.CreateQuiz)
                    },
                    navigateToUserDetails = {
                        navigateToUserDetails(it)
                    },
                    navigateToQuizDetails = { id ->
                        quizzesBackstack.add(Quizzes.QuizDetails(id))
                    },
                    quizzesListViewModel = quizzesListViewModel
                )
            }
            entry<Quizzes.QuizDetails> {
                QuizDetailsScreen(
                    id = it.id,
                    navigateToPlayScreen = {
                        navigateToGameScreen()
                    },
                    navigateBack = { navigateBack() }
                )
            }
            entry<Quizzes.CreateQuiz> {
                CreateQuizScreen(
                    navigateToQuestionCreate = { id ->
                        quizzesBackstack.add(Quizzes.CreateQuestion(id))
                    },
                    navigateBack = {
                        navigateBack()
                    },
                    createQuizViewModel = createQuizViewModel
                )
            }
            entry<Quizzes.CreateQuestion> {
                CreateQuestionScreen(
                    it.isMultiple,
                    createQuizViewModel = createQuizViewModel,
                    navigateBack = { quizzesBackstack.removeLastOrNull() }
                )
            }
        }
    )
}