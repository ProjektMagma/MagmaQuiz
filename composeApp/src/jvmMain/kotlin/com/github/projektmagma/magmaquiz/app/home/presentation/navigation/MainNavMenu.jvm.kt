package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.core.MainWindow
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.components.AnimatedVisibilityFloatingButton
import com.github.projektmagma.magmaquiz.app.home.presentation.components.NavButton
import com.github.projektmagma.magmaquiz.app.home.presentation.components.ProfilePictureIcon
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun MainNavMenu(
    mainBackStack: NavBackStack<NavKey>,
    quizzesBackstack: NavBackStack<NavKey>,
    navigateToHome: () -> Unit,
    navigateToQuizzes: () -> Unit,
    navigateToUsers: () -> Unit,
    navigateToUserProfile: () -> Unit,
    content: @Composable (() -> Unit)
) {

    val viewModel = koinViewModel<AuthViewModel>()
    val thisUser = viewModel.thisUser.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            AnimatedVisibilityFloatingButton(
                isShown = quizzesBackstack.last() == Route.Menus.Quizzes.Find &&
                        mainBackStack.last() is Route.Menus.Quizzes,
                onClick = {
                    quizzesBackstack.add(Route.Menus.Quizzes.CreateQuiz)
                }
            )
        },
        topBar = {
            MainWindow.frameWindowScope.WindowDraggableArea {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .weight(1f)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NavButton(
                                isCurrentRoute = mainBackStack.last() == Route.Menus.Home,
                                onClick = {
                                    navigateToHome()
                                },
                                contentLabel = stringResource(Res.string.home_nav),
                                contentIcon = Icons.Default.Home
                            )
                            NavButton(
                                isCurrentRoute = mainBackStack.last() is Route.Menus.Quizzes,
                                onClick = {
                                    navigateToQuizzes()
                                },
                                contentLabel = stringResource(Res.string.quizzes_nav),
                                contentIcon = Icons.Default.Quiz
                            )
                            NavButton(
                                isCurrentRoute = mainBackStack.last() is Route.Menus.Users,
                                onClick = {
                                    navigateToUsers()
                                },
                                contentLabel = stringResource(Res.string.users_nav),
                                contentIcon = Icons.Default.Groups
                            )
                        }
                        Text(
                            text = "${stringResource(Res.string.greeting)}, ${thisUser.value!!.userName}!",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        IconButton(
                            onClick = { navigateToUserProfile() },
                        ) {
                            ProfilePictureIcon(thisUser.value!!.userProfilePicture)
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                    }

                    WindowActionsButtonsRow()
                }
            }
        }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}