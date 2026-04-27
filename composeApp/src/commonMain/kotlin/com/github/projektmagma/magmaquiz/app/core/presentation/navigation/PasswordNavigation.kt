package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.change.PasswordChangeScreen
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.entry.PasswordEmailEntry
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.verify.PasswordVerifyScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun PasswordNavigation(
    startRoute: Route,
    navigateBack: () -> Unit
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration { 
            serializersModule = SerializersModule { 
                polymorphic(NavKey::class){
                    subclass(Route.Password.PasswordChange::class, Route.Password.PasswordChange.serializer())
                    subclass(Route.Password.PasswordVerification::class, Route.Password.PasswordVerification.serializer())
                    subclass(Route.Password.PasswordEmailEntry::class, Route.Password.PasswordEmailEntry.serializer())
                }
            }
        },
        startRoute
    )
    
    Column { 
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Route.Password.PasswordChange> {
                    PasswordChangeScreen(
                        forgot = it.forgot,
                        navigateToPasswordVerification = {
                            backStack.add(Route.Password.PasswordVerification)
                        },
                        navigateBack = { navigateBack() }
                    )
                }
                entry<Route.Password.PasswordEmailEntry> {
                    PasswordEmailEntry(
                        navigateToPasswordVerification = { 
                            backStack.add(Route.Password.PasswordVerification)
                        }
                    )
                }
                entry<Route.Password.PasswordVerification> {
                    PasswordVerifyScreen(
                        navigateToPasswordChange = {
                            backStack.clear()
                            backStack.add(Route.Password.PasswordChange(true))
                        }
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
}