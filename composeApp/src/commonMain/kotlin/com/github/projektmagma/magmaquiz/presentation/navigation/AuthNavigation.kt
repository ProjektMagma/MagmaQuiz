package com.github.projektmagma.magmaquiz.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.presentation.LoginScreen
import com.github.projektmagma.magmaquiz.presentation.RegisterScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun AuthNavigation(
    navigateToMain: () -> Unit
) {
    val authBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Auth.Login::class, Route.Auth.Login.serializer())
                    subclass(Route.Auth.Register::class, Route.Auth.Register.serializer())
                }
            }
        },
        Route.Auth.Login
    )

    NavDisplay(
        backStack = authBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Auth.Login> {
                LoginScreen(
                    navigateToRegister = {
                        authBackStack.add(Route.Auth.Register)
                    },
                    navigateToHome = {
                        navigateToMain()
                    }
                )
            }
            entry<Route.Auth.Register> {
                RegisterScreen(
                    navigateToLogin = {
                        authBackStack.add(Route.Auth.Login)
                    }
                )
            }
        }
    )
}