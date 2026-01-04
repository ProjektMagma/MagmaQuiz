package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route.Main.Quizzes
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.QuizDetailsScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.QuizzesScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun QuizzesNavigation() {
    val quizzesBackstack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Quizzes.Find::class, Quizzes.Find.serializer())
                    subclass(Quizzes.Details::class, Quizzes.Details.serializer())
                    subclass(Quizzes.Favorites::class, Quizzes.Favorites.serializer())
                }
            }
        },
        Quizzes.Find
    )

    NavDisplay(
        backStack = quizzesBackstack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Quizzes.Find> {
                QuizzesScreen(
                    navigateToQuizDetails = { id ->
                        quizzesBackstack.add(Quizzes.Details(id))
                    }
                )
            }
            entry<Quizzes.Details> {
                QuizDetailsScreen(
                    id = it.id
                )
            }
            entry<Quizzes.Favorites> {

            }
        }
    )
}