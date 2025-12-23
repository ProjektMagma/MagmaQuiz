package com.github.projektmagma.magmaquiz.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavRoot(modifier: Modifier = Modifier) {
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Auth::class, Route.Auth.serializer())
                    subclass(Route.Main::class, Route.Main.serializer())
                }
            }
        },
Route.Auth
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