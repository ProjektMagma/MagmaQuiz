package com.github.projektmagma.magmaquiz.app.settings.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.auth.presentation.components.EmailTextField
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.settings.presentation.EmailChangeViewModel
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.email.change.EmailChangeCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.change_email
import magmaquiz.composeapp.generated.resources.current_email
import magmaquiz.composeapp.generated.resources.new_email
import magmaquiz.composeapp.generated.resources.save
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmailChangeScreen(
    emailChangeViewModel: EmailChangeViewModel = koinViewModel(),
    navigateToEmailVerification: (email: String) -> Unit
) {
    val state by emailChangeViewModel.state.collectAsStateWithLifecycle()
    val user by emailChangeViewModel.user.collectAsStateWithLifecycle()
    val currentEmail = user?.userEmail ?: ""

    LaunchedEffect(Unit) {
        emailChangeViewModel.event.collect { event ->
            when (event) {
                is NetworkEvent.Failure -> SnackbarController.onEvent(getString(event.networkError.toResId()))
                NetworkEvent.Success -> navigateToEmailVerification(state.email)
            }
        }
    }

    val canSave = state.email.isNotBlank() && state.emailError == null

    Column(
        modifier = Modifier
            .widthIn(max = 560.dp)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.change_email),
            style = MaterialTheme.typography.headlineSmall
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(Res.string.current_email),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = currentEmail,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.new_email),
                    style = MaterialTheme.typography.labelLarge
                )

                EmailTextField(
                    emailText = state.email,
                    error = state.emailError,
                    onValueChange = {
                        emailChangeViewModel.onCommand(EmailChangeCommand.ChangeEmail(it))
                    }
                )
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = canSave,
            onClick = { emailChangeViewModel.onCommand(EmailChangeCommand.CheckIsEmailTaken) }
        ) {
            Text(text = stringResource(Res.string.save))
        }
    }
}