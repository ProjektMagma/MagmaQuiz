package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.home.presentation.navigation.MainNavigation
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    rootViewModel: RootViewModel = koinViewModel()
) {
    val uiState by rootViewModel.state.collectAsStateWithLifecycle()
    
    when (uiState) {
        UiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }
        UiState.Unauthenticated -> {
            AppNavigation(
                Route.Auth,
                modifier
            )
        }
        UiState.Authenticated -> {
            AppNavigation(
                Route.Main,
                modifier
            )
        }
    }
}

@Composable
private fun AppNavigation(
    startRoute: Route,
    modifier: Modifier = Modifier
){
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Auth::class, Route.Auth.serializer())
                    subclass(Route.Main::class, Route.Main.serializer())
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
                        rootBackStack.add(Route.Main)
                    }
                )
            }
            entry<Route.Main> {
                MainNavigation(
                    navigateToAuth = {
                        rootBackStack.clear()
                        rootBackStack.add(Route.Auth)
                    }
                )
            }
        }
    )
}