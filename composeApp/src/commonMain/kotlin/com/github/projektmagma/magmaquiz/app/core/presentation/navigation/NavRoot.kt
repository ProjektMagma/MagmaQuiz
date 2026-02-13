package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.auth.presentation.navigation.AuthNavigation
import com.github.projektmagma.magmaquiz.app.core.presentation.RootViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.AuthState
import com.github.projektmagma.magmaquiz.app.game.presentation.navigation.GameNavigation
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    rootViewModel: RootViewModel = koinViewModel()
) {
    val mainBackstack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Game::class, Route.Game.serializer())
                    subclass(Route.Auth::class, Route.Auth.serializer())
                    subclass(Route.Menus::class, Route.Menus.serializer())
                }
            }
        },
        Route.Auth
    )

    val authState by rootViewModel.state.collectAsStateWithLifecycle()

    when (authState) {
        AuthState.Loading -> {
            Column(modifier = Modifier.fillMaxSize()) {
                CustomWindowDraggableArea()
                FullSizeCircularProgressIndicator()
            }
        }

        AuthState.Unauthenticated -> Navigation(mainBackstack, modifier)

        AuthState.Authenticated -> {
            mainBackstack.add(Route.Menus)
            Navigation(mainBackstack, modifier)
        }
    }
}

@Composable
fun Navigation(
    mainBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    authRepository: AuthRepository = koinInject()
) {
    val navigationState = rememberNavigationState(
        startRoute = Route.Menus.Home,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
    )

    val navigator = remember {
        Navigator(navigationState)
    }
    
    NavDisplay(
        modifier = modifier,
        backStack = mainBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Auth> {
                AuthNavigation(
                    navigateToMain = {
                        mainBackStack.clear()
                        mainBackStack.add(Route.Menus)
                    }
                )
            }
            entry<Route.Menus> {
                MainNavMenu(
                    navigator = navigator,
                    navigationState = navigationState,
                    navigateToUserProfile = { navigator.navigate(Route.Menus.Users.UserDetails(authRepository.thisUser.value?.userId!!)) },
                ) {
                    MainNavigation(
                        navigator = navigator,
                        navigationState = navigationState,
                        navigateToAuth = {
                            mainBackStack.clear()
                            navigationState.resetAllBackStacks()
                            mainBackStack.add(Route.Auth)
                        },
                        navigateToGameScreen = { mainBackStack.add(Route.Game) }
                    )
                }
            }
            entry<Route.Game> {
                GameNavigation(
                    navigateToHome = { mainBackStack.add(Route.Menus) }
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