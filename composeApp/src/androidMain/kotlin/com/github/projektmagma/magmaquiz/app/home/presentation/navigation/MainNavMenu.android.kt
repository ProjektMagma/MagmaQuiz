package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.core.di.Navigator
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.components.AnimatedVisibilityFloatingButton
import com.github.projektmagma.magmaquiz.app.home.presentation.components.ProfilePictureIcon
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun MainNavMenu(
    navigator: Navigator,
    navigateToHome: () -> Unit,
    navigateToQuizzes: () -> Unit,
    navigateToUsers: () -> Unit,
    navigateToUserProfile: () -> Unit,
    content: @Composable (() -> Unit)
) {
    val viewModel = koinViewModel<AuthViewModel>()
    val thisUser = viewModel.thisUser.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AnimatedVisibilityFloatingButton(
                isShown = navigator.backstack.last() == Route.Menus.Quizzes.QuizzesList,
                animationPositionMultiplier = 4,
                onClick = {
                    navigator.goTo(Route.Menus.Quizzes.CreateQuiz)
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
            ) {
                NavigationBarItem(
                    onClick = {
                        navigateToHome()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "HomeButton",
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(Res.string.home_nav),
                            textAlign = TextAlign.Center
                        )
                    },
                    selected = navigator.backstack.last() == Route.Menus.Home,
                )
                NavigationBarItem(
                    onClick = {
                        navigateToQuizzes()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "QuizzesButton",
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(Res.string.quizzes_nav),
                            textAlign = TextAlign.Center
                        )
                    },
                    selected = navigator.backstack.last() is Route.Menus.Quizzes,
                )
                NavigationBarItem(
                    onClick = {
                        navigateToUsers()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "UsersButton",
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(Res.string.users_nav),
                            textAlign = TextAlign.Center
                        )
                    },
                    selected = navigator.backstack.last() is Route.Menus.Users,
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(Res.string.greeting)}, ${thisUser.value!!.userName}!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { navigateToUserProfile() },
                ) {
                    ProfilePictureIcon(thisUser.value!!.userProfilePicture)
                }
            }
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