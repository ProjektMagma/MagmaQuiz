package com.github.projektmagma.magmaquiz.app.auth.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.auth.presentation.components.EmailTextField
import com.github.projektmagma.magmaquiz.app.auth.presentation.components.NavigationAuthText
import com.github.projektmagma.magmaquiz.app.auth.presentation.components.PasswordTextField
import com.github.projektmagma.magmaquiz.app.auth.presentation.components.UsernameTextField
import com.github.projektmagma.magmaquiz.app.auth.presentation.model.auth.AuthCommand
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.ErrorMessageContext
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.have_an_account
import magmaquiz.composeapp.generated.resources.log_in
import magmaquiz.composeapp.generated.resources.register
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit
) {
    val viewModel = koinViewModel<AuthViewModel>()
    val state = viewModel.state

    LaunchedEffect(viewModel.authChannel) {
        viewModel.authChannel.collect { event ->
            when (event) {
                is NetworkEvent.Failure -> {
                    SnackbarController.onEvent(getString(event.networkError.toResId(ErrorMessageContext.Auth)))
                }

                NetworkEvent.Success -> {
                    navigateToHome()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .imePadding()
            .fillMaxSize()
            .widthIn(max = 512.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {

        Text(text = stringResource(Res.string.register))

        UsernameTextField(
            usernameText = state.username,
            usernameError = state.usernameError,
            imeAction = ImeAction.Next
        ) {
            viewModel.onCommand(AuthCommand.UsernameChanged(it))
        }

        EmailTextField(
            emailText = state.email,
            emailError = state.emailError,
            imeAction = ImeAction.Next
        ) {
            viewModel.onCommand(AuthCommand.EmailChanged(it))
        }

        PasswordTextField(
            passwordText = state.password,
            passwordError = state.passwordError,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = {
                viewModel.onCommand(AuthCommand.Register)
            })
        ) {
            viewModel.onCommand(AuthCommand.PasswordChanged(it))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.onCommand(AuthCommand.Register)
            }
        ) {
            Text(text = stringResource(Res.string.register))
        }

        NavigationAuthText(
            text1 = stringResource(Res.string.have_an_account),
            text2 = stringResource(Res.string.log_in)
        ) {
            navigateToLogin()
        }
    }
}
