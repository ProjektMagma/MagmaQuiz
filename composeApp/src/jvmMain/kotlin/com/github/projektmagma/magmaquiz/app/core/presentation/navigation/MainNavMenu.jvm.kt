package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.core.MainWindow
import com.github.projektmagma.magmaquiz.app.core.presentation.components.AnimatedVisibilityFloatingButton
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.greeting
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun MainNavMenu(
    navigator: Navigator,
    navigationState: NavigationState,
    navigateToUserProfile: () -> Unit,
    content: @Composable (() -> Unit)
) {

    val viewModel = koinViewModel<AuthViewModel>()
    val thisUser = viewModel.thisUser.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            AnimatedVisibilityFloatingButton(
                isShown = navigator.currentBackStack()
                    .last() == Route.Menus.Quizzes.QuizList, // Zostaw, bo to działa
                onClick = {
                    navigator.navigate(Route.Menus.Quizzes.CreateQuiz)
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
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                modifier = Modifier.size(50.dp),
                                onClick = navigator::goBack,
                                enabled = navigator.currentBackStack().size > 1
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                    tint = if (navigator.currentBackStack().size > 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                                    contentDescription = "BackButton",
                                )
                            }
                            QuizNavigationBar(
                                selectedKey = navigationState.topLevelRoute,
                                onSelectKey = {
                                    navigator.navigate(it)
                                }
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
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}