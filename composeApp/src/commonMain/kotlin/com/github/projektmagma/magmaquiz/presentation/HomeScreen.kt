package com.github.projektmagma.magmaquiz.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.presentation.model.AuthCommand
import com.github.projektmagma.magmaquiz.presentation.model.AuthEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    navigateToLogin: () -> Unit
) {
    val viewModel = koinViewModel<AuthViewModel>()
    val user = viewModel.user.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.authChannel) {
        viewModel.authChannel.collect { event ->
            when (event) {
                is AuthEvent.Failure -> {
                    snackbarHostState.showSnackbar(event.networkError.name)
                }

                AuthEvent.Success -> {
                    navigateToLogin()
                }
            }
        }
    }
    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Text("Hello ${user.value?.userName}!")

            Button(onClick = {
                viewModel.onCommand(AuthCommand.Logout)
            }) {
                Text(text = "Wyloguj")
            }
        }
    }
}