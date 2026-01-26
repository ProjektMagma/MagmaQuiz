package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthNavigation
import com.github.projektmagma.magmaquiz.app.core.presentation.RootViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.AuthState
import com.github.projektmagma.magmaquiz.app.home.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.home.presentation.navigation.CustomWindowDraggableArea
import com.github.projektmagma.magmaquiz.app.home.presentation.navigation.MainNavigation
import com.github.projektmagma.magmaquiz.app.home.presentation.screens.GameScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    rootViewModel: RootViewModel = koinViewModel()
) {
    val authState by rootViewModel.state.collectAsStateWithLifecycle()

    when (authState) {
        AuthState.Loading -> {
            Column(modifier = Modifier.fillMaxSize()) {
                CustomWindowDraggableArea()
                FullSizeCircularProgressIndicator()
            }
        }

        AuthState.Unauthenticated -> {
            AppNavigation(
                Route.Auth,
                modifier
            )
        }

        AuthState.Authenticated -> {
            AppNavigation(
                Route.Menus,
                modifier
            )
        }
    }
}

@Composable
private fun AppNavigation(
    startRoute: Route,
    modifier: Modifier = Modifier
) {
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Auth::class, Route.Auth.serializer())
                    subclass(Route.Menus::class, Route.Menus.serializer())
                    subclass(Route.Game::class, Route.Game.serializer())
                }
            }
        },
        startRoute
    )

    NavDisplay(
        modifier = modifier,
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Auth> {
                AuthNavigation(
                    navigateToMain = {
                        rootBackStack.clear()
                        rootBackStack.add(Route.Menus)
                    }
                )
            }
            entry<Route.Menus> {
                MainNavigation(
                    navigateToGameScreen = {
                        rootBackStack.clear()
                        rootBackStack.add(Route.Game)
                    },
                    navigateToAuth = {
                        rootBackStack.clear()
                        rootBackStack.add(Route.Auth)
                    }
                )
            }
            entry<Route.Game> {
                GameScreen(
                    navigateOnGameFinish = {
                        rootBackStack.clear()
                        rootBackStack.add(Route.Menus)
                    }
                )
            }
        }
    )
}