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
import com.github.projektmagma.magmaquiz.presentation.model.auth.AuthCommand
import com.github.projektmagma.magmaquiz.presentation.model.auth.AuthEvent
import com.github.projektmagma.magmaquiz.util.SnackbarController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit
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
                    navigateToHome()
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .padding(horizontal = 48.dp)
            .fillMaxSize()
            .widthIn(max = 512.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text(text = "Login")

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
                viewModel.onCommand(AuthCommand.Login)
            }
        ) {
            Text(text = "Zaloguj")
        }

        NavigationAuthText(
            text1 = "Nie masz konta",
            text2 = "Zarejestruj sie"
        ) {
            navigateToRegister()
        }
    }
}