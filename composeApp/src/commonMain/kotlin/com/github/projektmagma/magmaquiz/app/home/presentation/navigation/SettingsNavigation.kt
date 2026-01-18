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
import com.github.projektmagma.magmaquiz.app.home.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.SettingsScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.CreateQuestionScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes.CreateQuizScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsNavigation(
    navigateToAuth: () -> Unit
) {
    val settingsBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class){
                    subclass(Route.Main.Settings.Edit::class, Route.Main.Settings.Edit.serializer())
                    subclass(Route.Main.Settings.Profile::class, Route.Main.Settings.Profile.serializer())
                    subclass(Route.Main.Settings.EditQuestion::class, Route.Main.Settings.EditQuestion.serializer())
                }
            }
        },
        Route.Main.Settings.Profile
    )

    val createQuizViewModel: CreateQuizViewModel = koinViewModel()
    
    NavDisplay(
        backStack = settingsBackStack,
        entryDecorators = listOf(
            rememberViewModelStoreNavEntryDecorator(),
            rememberSaveableStateHolderNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Main.Settings.Edit> {
                CreateQuizScreen(
                    navigateToQuestionCreate = { settingsBackStack.add(Route.Main.Settings.EditQuestion(it)) },
                    navigateBack = { settingsBackStack.removeLastOrNull() },
                    createQuizViewModel = createQuizViewModel
                )
            }
            entry<Route.Main.Settings.Profile> {
                SettingsScreen(
                    navigateToAuth = { navigateToAuth() },
                    navigateToEditScreen = {settingsBackStack.add(Route.Main.Settings.Edit)},
                    createQuizViewModel = createQuizViewModel
                )
            }
            entry<Route.Main.Settings.EditQuestion> {
                CreateQuestionScreen(
                    it.isMultiple,
                    createQuizViewModel = createQuizViewModel,
                    navigateBack = { settingsBackStack.removeLastOrNull() }
                )
            }
        }
    )
}