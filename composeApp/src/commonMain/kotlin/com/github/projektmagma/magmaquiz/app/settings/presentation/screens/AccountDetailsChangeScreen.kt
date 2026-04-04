package com.github.projektmagma.magmaquiz.app.settings.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.auth.presentation.components.UsernameTextField
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.LocalEvent
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.settings.presentation.AccountDetailsChangeViewModel
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.account.AccountDetailsChangeCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.account_details_change
import magmaquiz.composeapp.generated.resources.bio
import magmaquiz.composeapp.generated.resources.cant_save
import magmaquiz.composeapp.generated.resources.save
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountDetailsChangeScreen(
    accountDetailsChangeViewModel: AccountDetailsChangeViewModel = koinViewModel(),
    goBack: () -> Unit
) {
    val state by accountDetailsChangeViewModel.state.collectAsStateWithLifecycle()

    val canSave = state.usernameError == null

    LaunchedEffect(Unit){
        accountDetailsChangeViewModel.event.collect { event ->
            when (event) {
                LocalEvent.Failure -> {
                    SnackbarController.onEvent(getString(Res.string.cant_save))
                }
                LocalEvent.Success -> { goBack() }
            }
        }
    }

    Column(
        modifier = Modifier
            .widthIn(max = 1000.dp)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = stringResource(Res.string.account_details_change),
            style = MaterialTheme.typography.headlineSmall
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UsernameTextField(
                    usernameText = state.username,
                    error = state.usernameError,
                    onValueChange = {
                        accountDetailsChangeViewModel.onCommand(
                            AccountDetailsChangeCommand.UsernameChanged(it)
                        )
                    }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.bio,
                    onValueChange = {
                        accountDetailsChangeViewModel.onCommand(
                            AccountDetailsChangeCommand.BioChanged(it)
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    label = { Text(stringResource(Res.string.bio)) },
                )
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = canSave,
            onClick = { accountDetailsChangeViewModel.onCommand(AccountDetailsChangeCommand.Save) }
        ) {
            Text(stringResource(Res.string.save))
        }
    }
}
