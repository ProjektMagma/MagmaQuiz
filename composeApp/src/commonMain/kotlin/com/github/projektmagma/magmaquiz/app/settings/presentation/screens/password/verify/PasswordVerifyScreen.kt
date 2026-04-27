package com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.verify

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.util.ObserveAsEvents
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.verify.PasswordVerifyCommand
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.verify.PasswordVerifyEvent
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.change_password
import magmaquiz.composeapp.generated.resources.code_we_sent
import magmaquiz.composeapp.generated.resources.confirm
import magmaquiz.composeapp.generated.resources.couldnt_send_code
import magmaquiz.composeapp.generated.resources.couldnt_verify_code
import magmaquiz.composeapp.generated.resources.example_code
import magmaquiz.composeapp.generated.resources.invalid_code
import magmaquiz.composeapp.generated.resources.resend_code
import magmaquiz.composeapp.generated.resources.send_again_in
import magmaquiz.composeapp.generated.resources.sent_code
import magmaquiz.composeapp.generated.resources.verification_code
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PasswordVerifyScreen(
    navigateToPasswordChange: () -> Unit,
    passwordVerifyViewModel: PasswordVerifyViewModel = koinViewModel()
) {
    val state by passwordVerifyViewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(passwordVerifyViewModel.event) { event ->
        when (event) {
            PasswordVerifyEvent.CodeSent -> SnackbarController.onEvent(getString(Res.string.sent_code))
            PasswordVerifyEvent.Verified -> navigateToPasswordChange()
            PasswordVerifyEvent.FailureSent -> SnackbarController.onEvent(getString(Res.string.couldnt_send_code))
            PasswordVerifyEvent.FailureVerify -> SnackbarController.onEvent(getString(Res.string.couldnt_verify_code))
        }
    }

    Column(
        modifier = Modifier
            .widthIn(max = 560.dp)
            .fillMaxSize()
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
                Text(
                    text = stringResource(Res.string.code_we_sent),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = state.email,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.code,
                    onValueChange = { passwordVerifyViewModel.onCommand(PasswordVerifyCommand.CodeChanged(it)) },
                    singleLine = true,
                    isError = state.codeError != null,
                    supportingText = {
                        if (state.codeError != null) {
                            Text(stringResource(Res.string.invalid_code))
                        }
                    },
                    label = { Text(stringResource(Res.string.verification_code)) },
                    placeholder = { Text(stringResource(Res.string.example_code)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }

        val canConfirm = state.code.length == 6

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { passwordVerifyViewModel.onCommand(PasswordVerifyCommand.SendCode) },
            enabled = state.remainingSeconds == 0
        ) {
            Text(stringResource(Res.string.resend_code))
        }

        Text(
            modifier = Modifier.align(CenterHorizontally),
            text = stringResource(Res.string.send_again_in, state.remainingSeconds),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.labelSmall
        )


        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = canConfirm,
            onClick = { passwordVerifyViewModel.onCommand(PasswordVerifyCommand.Verify) }
        ) {
            Text(stringResource(Res.string.confirm))
        }
    }
}
