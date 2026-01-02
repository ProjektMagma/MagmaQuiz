package com.github.projektmagma.magmaquiz.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.projektmagma.magmaquiz.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.presentation.model.auth.AuthCommand
import com.github.projektmagma.magmaquiz.presentation.model.auth.AuthEvent
import com.github.projektmagma.magmaquiz.util.SnackbarController
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun SettingsScreen(navigateToAuth: () -> Unit) {

    val viewModel = koinViewModel<AuthViewModel>()

    LaunchedEffect(viewModel.authChannel) {
        viewModel.authChannel.collect { event ->
            when (event) {
                is AuthEvent.Failure -> {
                    SnackbarController.onEvent(event.networkError.name)
                }

                AuthEvent.Success -> {
                    navigateToAuth()
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            viewModel.onCommand(AuthCommand.Logout)
        }) {
            Text(text = "Wyloguj")
        }
    }
}