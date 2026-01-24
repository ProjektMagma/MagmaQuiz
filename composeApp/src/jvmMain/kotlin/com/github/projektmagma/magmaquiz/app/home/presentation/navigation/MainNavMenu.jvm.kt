package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.home.presentation.components.NavButton
import com.github.projektmagma.magmaquiz.app.home.presentation.components.ProfilePictureIcon
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun MainNavMenu(
    backStack: NavBackStack<NavKey>,
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
            val workInProgress = stringResource(Res.string.work_in_progress)
            if (backStack.last() is Route.Menus.Quizzes)
                IconButton(
                    modifier = Modifier.size(50.dp),
                    colors = IconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    onClick = {
                        SnackbarController.onEvent(workInProgress)
                    }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                    )
                }
        },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavButton(
                        isCurrentRoute = backStack.last() == Route.Menus.Home,
                        onClick = {
                            navigateToHome()
                        },
                        contentLabel = stringResource(Res.string.home_nav),
                        contentIcon = Icons.Default.Home
                    )
                    NavButton(
                        isCurrentRoute = backStack.last() is Route.Menus.Quizzes,
                        onClick = {
                            navigateToQuizzes()
                        },
                        contentLabel = stringResource(Res.string.quizzes_nav),
                        contentIcon = Icons.Default.Quiz
                    )
                    NavButton(
                        isCurrentRoute = backStack.last() is Route.Menus.Users,
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