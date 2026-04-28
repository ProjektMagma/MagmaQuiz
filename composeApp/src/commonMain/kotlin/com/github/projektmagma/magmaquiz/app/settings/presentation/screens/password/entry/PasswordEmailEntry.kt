package com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.entry

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
import com.github.projektmagma.magmaquiz.app.auth.presentation.components.EmailTextField
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.LocalEvent
import com.github.projektmagma.magmaquiz.app.core.util.ObserveAsEvents
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.entry.PasswordEmailEntryCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.change_password
import magmaquiz.composeapp.generated.resources.confirm
import magmaquiz.composeapp.generated.resources.email
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PasswordEmailEntry(
    navigateToPasswordVerification: () -> Unit,
    passwordEmailEntryViewModel: PasswordEmailEntryViewModel = koinViewModel()
) {
    val state by passwordEmailEntryViewModel.state.collectAsStateWithLifecycle()
    val canSend = state.email?.isNotBlank() == true && state.emailError == null

    ObserveAsEvents(passwordEmailEntryViewModel.event){ event ->
        when (event){
            LocalEvent.Failure -> Unit
            LocalEvent.Success -> navigateToPasswordVerification()
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.email),
                        style = MaterialTheme.typography.labelLarge
                    )

                    EmailTextField(
                        emailText = state.email ?: "",
                        error = state.emailError,
                        onValueChange = {
                            passwordEmailEntryViewModel.onCommand(PasswordEmailEntryCommand.EmailChanged(it))
                        }
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = canSend,
                onClick = {
                    passwordEmailEntryViewModel.onCommand(PasswordEmailEntryCommand.CheckEmail)
                }
            ) {
                Text(stringResource(Res.string.confirm))
            }
        }
    }
}
