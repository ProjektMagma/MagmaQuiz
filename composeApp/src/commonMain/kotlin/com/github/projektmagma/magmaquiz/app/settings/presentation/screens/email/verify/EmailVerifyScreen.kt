package com.github.projektmagma.magmaquiz.app.settings.presentation.screens.email.verify

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.email.verify.EmailVerifyCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.code_we_sent
import magmaquiz.composeapp.generated.resources.confirm
import magmaquiz.composeapp.generated.resources.email_verification
import magmaquiz.composeapp.generated.resources.example_code
import magmaquiz.composeapp.generated.resources.resend_code
import magmaquiz.composeapp.generated.resources.send_again_in
import magmaquiz.composeapp.generated.resources.successful_email_change
import magmaquiz.composeapp.generated.resources.verification_code
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EmailVerificationScreen(
    email: String,
    navigateBack: () -> Unit
){
    val emailVerifyViewModel: EmailVerifyViewModel = koinViewModel { parametersOf(email) }
    
    LaunchedEffect(Unit){
        emailVerifyViewModel.event.collect { event -> 
            when (event) {
                is NetworkEvent.Failure -> SnackbarController.onEvent(getString(event.networkError.toResId()))
                NetworkEvent.Success -> {
                    SnackbarController.onEvent(getString(resource = Res.string.successful_email_change))
                    navigateBack()
                }
            }
        }
    }
    
    val state by emailVerifyViewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(Res.string.email_verification),
                style = MaterialTheme.typography.headlineSmall
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
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

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.code,
                        onValueChange = { input ->
                            emailVerifyViewModel.onCommand(EmailVerifyCommand.ChangeVerificationCode(input))
                        },
                        singleLine = true,
                        label = { Text(stringResource(Res.string.verification_code)) },
                        placeholder = { Text(stringResource(Res.string.example_code)) },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { emailVerifyViewModel.onCommand(EmailVerifyCommand.ResendCode) },
                enabled = state.timeToResend == 0
            ) {
                Text(stringResource(Res.string.resend_code))
            }
            
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(Res.string.send_again_in, state.timeToResend),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelSmall
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { emailVerifyViewModel.onCommand(EmailVerifyCommand.VerifyCode) }
            ) {
                Text(stringResource(Res.string.confirm))
            }
        }
    }
}