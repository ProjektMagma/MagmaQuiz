package com.github.projektmagma.magmaquiz.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.presentation.components.EmailTextField
import com.github.projektmagma.magmaquiz.presentation.components.NavigationAuthText
import com.github.projektmagma.magmaquiz.presentation.components.PasswordTextField
import com.github.projektmagma.magmaquiz.presentation.components.UsernameTextField
import com.github.projektmagma.magmaquiz.presentation.model.auth.AuthCommand
import com.github.projektmagma.magmaquiz.presentation.model.auth.AuthEvent
import com.github.projektmagma.magmaquiz.util.SnackbarController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    navigateToLogin: () -> Unit
) {
    val viewModel = koinViewModel<AuthViewModel>()
    val state = viewModel.state

    LaunchedEffect(viewModel.authChannel) {
        viewModel.authChannel.collect { event ->
            when (event) {
                is AuthEvent.Failure -> {
                    SnackbarController.onEvent(event.networkError.name)
                }

                AuthEvent.Success -> {

                }
            }
        }
    }
    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxSize()
            .widthIn(max = 512.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text(text = "Register")

        UsernameTextField(
            usernameText = state.username,
            usernameError = state.usernameError
        ) {
            viewModel.onCommand(AuthCommand.UsernameChanged(it))
        }

        EmailTextField(
            emailText = state.email,
            emailError = state.emailError
        ) {
            viewModel.onCommand(AuthCommand.EmailChanged(it))
        }

        PasswordTextField(
            passwordText = state.password,
            passwordError = state.passwordError
        ) {
            viewModel.onCommand(AuthCommand.PasswordChanged(it))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.onCommand(AuthCommand.Register)
            }
        ) {
            Text(text = "Zarejestruj")
        }

        NavigationAuthText(
            text1 = "Masz juz konto?",
            text2 = "Zaloguj sie"
        ) {
            navigateToLogin()
        }
    }
}