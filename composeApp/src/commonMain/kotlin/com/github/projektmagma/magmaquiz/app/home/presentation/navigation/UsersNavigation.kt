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
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.UserDetailsScreen
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.UsersScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID
import kotlin.collections.listOf

@Composable
fun UsersNavigation(
    startDestination: Route,
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToEditScreen: (id: UUID) -> Unit,
    navigateToSettingScreen: () -> Unit,
) {
    val usersNavBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Menus.Users.UserDetails::class, Route.Menus.Users.UserDetails.serializer())
                    subclass(Route.Menus.Users.Find::class, Route.Menus.Users.Find.serializer())
                    subclass(Route.Menus.Users.Friends::class, Route.Menus.Users.Friends.serializer())
                }
            }
        },
        startDestination
    )

    val createQuizViewModel: CreateQuizViewModel = koinViewModel()

    NavDisplay(
        backStack = usersNavBackStack,
        entryDecorators = listOf(
            rememberViewModelStoreNavEntryDecorator(),
            rememberSaveableStateHolderNavEntryDecorator()
        ),
        entryProvider = entryProvider { 
            entry<Route.Menus.Users.UserDetails> { parameters ->
                UserDetailsScreen(
                    id = parameters.id,
                    createQuizViewModel = createQuizViewModel,
                    navigateToEditScreen = { navigateToEditScreen(it) },
                    navigateToQuizDetails = { navigateToQuizDetails(it) },
                    navigateToSettingsScreen = { navigateToSettingScreen() }
                )
            }
            entry<Route.Menus.Users.Find> {
                UsersScreen(navigateToUserDetails = { usersNavBackStack.add(Route.Menus.Users.UserDetails(it)) })
            }
            entry<Route.Menus.Users.Friends> {

            }
        }
    )
}