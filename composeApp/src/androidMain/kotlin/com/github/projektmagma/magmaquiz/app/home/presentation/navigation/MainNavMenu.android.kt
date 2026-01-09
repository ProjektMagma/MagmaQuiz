package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.components.ProfilePictureIcon
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.greeting
import magmaquiz.composeapp.generated.resources.home_nav
import magmaquiz.composeapp.generated.resources.quizzes_nav
import magmaquiz.composeapp.generated.resources.users_nav
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun MainNavMenu(
    backStack: NavBackStack<NavKey>,
    navigateToHome: () -> Unit,
    navigateToQuizzes: () -> Unit,
    navigateToUsers: () -> Unit,
    navigateToSettings: () -> Unit,
    content: @Composable (() -> Unit)
) {
    val viewModel = koinViewModel<AuthViewModel>()
    val thisUser = viewModel.thisUser.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(25.dp))
            Text(
                text = "${stringResource(Res.string.greeting)}, ${thisUser.value!!.userName}!",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(
                onClick = { navigateToSettings() },
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
                selected = backStack.last() == Route.Main.Home,
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
                selected = backStack.last() == Route.Main.Quizzes,
            )
            NavigationBarItem(
                onClick = {
                    navigateToUsers()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
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
                selected = backStack.last() == Route.Main.Users,
            )
        }
    }
}