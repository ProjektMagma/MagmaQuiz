package com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.change

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.auth.presentation.components.ForgotPasswordText
import com.github.projektmagma.magmaquiz.app.auth.presentation.components.PasswordTextField
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.ObserveAsEvents
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.change.PasswordChangeCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.change_password
import magmaquiz.composeapp.generated.resources.couldnt_change_password
import magmaquiz.composeapp.generated.resources.current_password
import magmaquiz.composeapp.generated.resources.new_password
import magmaquiz.composeapp.generated.resources.password
import magmaquiz.composeapp.generated.resources.passwords_dont_match
import magmaquiz.composeapp.generated.resources.repeat_new_password
import magmaquiz.composeapp.generated.resources.save
import magmaquiz.composeapp.generated.resources.successfuly_changed_password
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PasswordChangeScreen(
    forgot: Boolean,
    navigateToPasswordVerification: () -> Unit,
    navigateBack: () -> Unit,
) {
    val passwordChangeViewModel: PasswordChangeViewModel = koinViewModel { parametersOf(forgot) }
    val state by passwordChangeViewModel.state.collectAsStateWithLifecycle()
    
    ObserveAsEvents(passwordChangeViewModel.event){ event -> 
        when (event){
            is NetworkEvent.Failure -> SnackbarController.onEvent(getString(Res.string.couldnt_change_password))
            NetworkEvent.Success -> {
                SnackbarController.onEvent(getString(Res.string.successfuly_changed_password))
                navigateBack()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.change_password),
                style = MaterialTheme.typography.headlineSmall
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!forgot) {
                        Text(
                            text = stringResource(Res.string.current_password),
                            style = MaterialTheme.typography.labelLarge
                        )
                        PasswordTextField(
                            passwordText = state.oldPassword,
                            error = state.oldPasswordError,
                            onValueChange = { passwordChangeViewModel.onCommand(PasswordChangeCommand.OldPasswordChanged(it)) }
                        )
                    }

                    Text(
                        text = if (forgot) stringResource(Res.string.password) else stringResource(Res.string.new_password),
                        style = MaterialTheme.typography.labelLarge
                    )
                    PasswordTextField(
                        passwordText = state.newPassword,
                        error = state.newPasswordError,
                        onValueChange = { passwordChangeViewModel.onCommand(PasswordChangeCommand.NewPasswordChanged(it)) }
                    )
                    
                    Text(
                        text = stringResource(Res.string.repeat_new_password),
                        style = MaterialTheme.typography.labelLarge
                    )
                    PasswordTextField(
                        passwordText = state.repeatedPassword,
                        error = null,
                        onValueChange = {
                            passwordChangeViewModel.onCommand(PasswordChangeCommand.RepeatedPasswordChange(it))
                        }
                    )

                    if (state.passwordMatch == false) {
                        Text(
                            text = stringResource(Res.string.passwords_dont_match),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (!forgot) {
                        ForgotPasswordText(
                            navigateToEmailSettings = { navigateToPasswordVerification() }
                        )
                    }
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    passwordChangeViewModel.onCommand(PasswordChangeCommand.Save)
                }
            ) {
                Text(text = stringResource(Res.string.save))
            }
        }
    }
}
