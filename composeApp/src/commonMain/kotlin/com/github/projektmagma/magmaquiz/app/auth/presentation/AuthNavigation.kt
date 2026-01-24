package com.github.projektmagma.magmaquiz.app.auth.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.app.auth.presentation.screens.LoginScreen
import com.github.projektmagma.magmaquiz.app.auth.presentation.screens.OnBoardingScreen
import com.github.projektmagma.magmaquiz.app.auth.presentation.screens.RegisterScreen
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.core.presentation.screens.ServerConfigScreen
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
                    subclass(Route.Auth.OnBoarding::class, Route.Auth.OnBoarding.serializer())
                    subclass(Route.Auth.ServerConfig::class, Route.Auth.ServerConfig.serializer())
                }
            }
        },
        Route.Auth.OnBoarding
    )
    
    fun navigateToRegister() {
        authBackStack.add(Route.Auth.Register)
    }
    
    fun navigateToLogin() {
        authBackStack.add(Route.Auth.Login)
    }

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
                        navigateToRegister()
                    },
                    navigateToHome = {
                        navigateToMain()
                    }
                )
            }
            entry<Route.Auth.Register> {
                RegisterScreen(
                    navigateToLogin = {
                        navigateToLogin()
                    },
                    navigateToHome = {
                        navigateToMain()
                    }
                )
            }
            entry<Route.Auth.OnBoarding> {
                OnBoardingScreen(
                    navigateToLogin = { navigateToLogin() },
                    navigateToRegister = { navigateToRegister() },
                    navigateToServerConfig = { authBackStack.add(Route.Auth.ServerConfig) }
                )
            }
            entry<Route.Auth.ServerConfig> {
                ServerConfigScreen(
                    navigateBack = {
                        authBackStack.removeAt(authBackStack.count() - 1)
                    }
                )
            }
        }
    )
}