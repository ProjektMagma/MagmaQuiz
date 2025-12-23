package com.github.projektmagma.magmaquiz.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.presentation.HomeScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun MainNavigation(
    navigateToAuth: () -> Unit
) {
    val mainBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration { 
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Main.Home::class, Route.Main.Home.serializer())
                }
            }
        },
        Route.Main.Home
    )

    NavDisplay(
        backStack = mainBackStack,
        entryDecorators = listOf(
            rememberViewModelStoreNavEntryDecorator(),
            rememberSaveableStateHolderNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Main.Home> { 
                HomeScreen(
                    navigateToLogin = {
                        navigateToAuth()
                    }
                )
            }
        }
    )
}