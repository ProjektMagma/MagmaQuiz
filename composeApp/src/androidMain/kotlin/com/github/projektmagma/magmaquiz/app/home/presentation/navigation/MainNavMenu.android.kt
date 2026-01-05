package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
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
import com.github.projektmagma.magmaquiz.app.home.presentation.components.ProfilePictureIcon
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(25.dp))
            Text(
                text = "Hello, ${thisUser.value!!.userName}!",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(
                onClick = { navigateToSettings() },
            ) {
                if (thisUser.value!!.userProfilePicture == null)
                    Icon(
                        modifier = Modifier.size(25.dp),
                        imageVector = Icons.Default.AccountCircle,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "SettingsButton",
                    )
                else
                    ProfilePictureIcon(thisUser.value!!.userProfilePicture!!)
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
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "HomeButton",
                    )
                },
                label = { Text("Home") },
                selected = backStack.last() == Route.Main.Home,
            )
            NavigationBarItem(
                onClick = {
                    navigateToQuizzes()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "QuizzesButton",
                    )
                },
                label = { Text("Quizzes") },
                selected = backStack.last() == Route.Main.Quizzes,
            )
            NavigationBarItem(
                onClick = {
                    navigateToUsers()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "UsersButton",
                    )
                },
                label = { Text("Users") },
                selected = backStack.last() == Route.Main.Users,
            )
        }
    }
}