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
import com.github.projektmagma.magmaquiz.presentation.OnBoardingScreen
import com.github.projektmagma.magmaquiz.presentation.RegisterScreen
import com.github.projektmagma.magmaquiz.presentation.ServerConfigScreen
import com.github.projektmagma.magmaquiz.presentation.navigation.Route.Auth
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
                    subclass(Auth.Login::class, Auth.Login.serializer())
                    subclass(Auth.Register::class, Auth.Register.serializer())
                    subclass(Auth.OnBoarding::class, Auth.OnBoarding.serializer())
                    subclass(Auth.ServerConfig::class, Auth.ServerConfig.serializer())
                }
            }
        },
        Auth.OnBoarding
    )
    
    fun navigateToRegister() {
        authBackStack.add(Auth.Register)
    }
    
    fun navigateToLogin() {
        authBackStack.add(Auth.Login)
    }

    NavDisplay(
        backStack = authBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Auth.Login> {
                LoginScreen(
                    navigateToRegister = {
                        navigateToRegister()
                    },
                    navigateToHome = {
                        navigateToMain()
                    }
                )
            }
            entry<Auth.Register> {
                RegisterScreen(
                    navigateToLogin = {
                        navigateToLogin()
                    }
                )
            }
            entry<Auth.OnBoarding> {
                OnBoardingScreen(
                    navigateToLogin = { navigateToLogin() },
                    navigateToRegister = { navigateToRegister() },
                    navigateToServerConfig = { authBackStack.add(Auth.ServerConfig) }
                )
            }
            entry<Auth.ServerConfig> {
                ServerConfigScreen(
                    navigateBack = {
                        authBackStack.removeAt(authBackStack.count() - 1)
                    }
                )
            }
        }
    )
}