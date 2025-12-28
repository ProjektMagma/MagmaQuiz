package com.github.projektmagma.magmaquiz.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.presentation.model.AuthCommand
import com.github.projektmagma.magmaquiz.presentation.model.AuthEvent
import com.github.projektmagma.magmaquiz.util.SnackbarController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    navigateToLogin: () -> Unit
) {
    val viewModel = koinViewModel<AuthViewModel>()
    val thisUser = viewModel.thisUser.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.authChannel) {
        viewModel.authChannel.collect { event ->
            when (event) {
                is AuthEvent.Failure -> {
                    SnackbarController.onEvent(event.networkError.name)
                }

                AuthEvent.Success -> {
                    navigateToLogin()
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Hello ${thisUser.value?.userName}!")

        Button(onClick = {
            viewModel.onCommand(AuthCommand.Logout)
        }) {
            Text(text = "Wyloguj")
        }
    }
}