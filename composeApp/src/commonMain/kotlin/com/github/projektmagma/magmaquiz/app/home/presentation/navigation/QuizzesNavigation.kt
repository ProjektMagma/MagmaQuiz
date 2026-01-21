package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route.Menus.Quizzes
import com.github.projektmagma.magmaquiz.app.home.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.CreateQuestionScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.CreateQuizScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.QuizDetailsScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.QuizzesScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun QuizzesNavigation(
    navigateToGameScreen: () -> Unit
) {
    val quizzesBackstack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Quizzes.Find::class, Quizzes.Find.serializer())
                    subclass(Quizzes.Details::class, Quizzes.Details.serializer())
//                    subclass(Quizzes.Game::class, Quizzes.Game.serializer())
                    subclass(Quizzes.CreateQuiz::class, Quizzes.CreateQuiz.serializer())
                    subclass(Quizzes.CreateQuestion::class, Quizzes.CreateQuestion.serializer())
                }
            }
        },
        Quizzes.Find
    )

    val quizViewModel: QuizViewModel = koinViewModel()
    val createQuizViewModel: CreateQuizViewModel = koinViewModel()

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
                    navigateToQuizDetails = { id ->
                        quizzesBackstack.add(Quizzes.Details(id))
                    }
                )
            }
            entry<Quizzes.Details> {
                QuizDetailsScreen(
                    id = it.id,
                    quizViewModel = quizViewModel,
                    navigateToPlayScreen = {
                        navigateToGameScreen()
                    },
                    navigateBack = { quizzesBackstack.removeLastOrNull() }
                )
            }
            entry<Quizzes.CreateQuiz> {
                CreateQuizScreen(
                    navigateToQuestionCreate = {
                        quizzesBackstack.add(Quizzes.CreateQuestion(it))
                    },
                    navigateBack = {
                        quizzesBackstack.removeLastOrNull()
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