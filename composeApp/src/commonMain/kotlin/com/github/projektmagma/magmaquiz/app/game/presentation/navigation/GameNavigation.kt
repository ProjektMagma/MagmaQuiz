package com.github.projektmagma.magmaquiz.app.game.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.CustomWindowDraggableArea
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.game.presentation.screens.GameLeaderboardScreen
import com.github.projektmagma.magmaquiz.app.game.presentation.screens.GameMultiplayerScreen
import com.github.projektmagma.magmaquiz.app.game.presentation.screens.GameScreen
import com.github.projektmagma.magmaquiz.app.game.presentation.screens.GameSettingsScreen
import com.github.projektmagma.magmaquiz.app.game.presentation.screens.GameWaitScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun GameNavigation(
    startDestination: Route,
    navigateToHome: () -> Unit
){
    val gameBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Game.Singleplayer::class, Route.Game.Singleplayer.serializer())
                    subclass(Route.Game.Multiplayer::class, Route.Game.Multiplayer.serializer())
                    subclass(Route.Game.Wait::class, Route.Game.Wait.serializer())
                    subclass(Route.Game.Settings::class, Route.Game.Settings.serializer())
                    subclass(Route.Game.Leaderboard::class, Route.Game.Leaderboard.serializer())
                }
            }
        },
        startDestination
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomWindowDraggableArea()
        NavDisplay(
            backStack = gameBackStack,
            entryProvider = entryProvider {
                entry<Route.Game.Singleplayer> {
                    GameScreen(
                        navigateOnGameFinish = { navigateToHome() }
                    )
                }
                entry<Route.Game.Multiplayer> {
                    GameMultiplayerScreen(
                        navigateOnGameFinish = { navigateToHome() }
                    )
                }
                entry<Route.Game.Wait> {
                    GameWaitScreen(
                        onStartGamePlayer = {
                            gameBackStack.removeLastOrNull()
                            gameBackStack.add(Route.Game.Multiplayer)
                        },
                        onStartGameHost = {
                            gameBackStack.removeLastOrNull()
                            gameBackStack.add(Route.Game.Leaderboard)
                        },
                        onLeaveRoom = { navigateToHome() }
                    )
                }
                entry<Route.Game.Settings> {
                    GameSettingsScreen(
                        navigateToHostScreen = {
                            gameBackStack.clear()
                            gameBackStack.add(Route.Game.Wait)
                        }
                    )
                }
                entry<Route.Game.Leaderboard> {
                    GameLeaderboardScreen(
                        navigateBack = { navigateToHome() }
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