package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun MainNavMenu(
    navigateToHome: () -> Unit,
    navigateToPlay: () -> Unit,
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
                // TODO: Tutaj zamiast ikonki, małe zdjęcie profilowe (64x64 lub 128x128), ale muszę to dodać na serwer
                Icon(
                    modifier = Modifier.size(25.dp),
                    imageVector = Icons.Default.AccountCircle,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "SettingsButton",
                )

            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                content()
            }
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
                selected = false,
            )
            NavigationBarItem(
                onClick = {
                    navigateToPlay()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.PlayCircleFilled,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "PlayButton",
                    )
                },
                label = { Text("Play") },
                selected = false,
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
                selected = false,
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
                selected = false,
            )
        }
    }
}