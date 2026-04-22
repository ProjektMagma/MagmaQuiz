package com.github.projektmagma.magmaquiz.app.game.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.game.presentation.GameSettingsViewModel
import com.github.projektmagma.magmaquiz.app.game.presentation.model.settings.GameSettingsCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.cant_create_room
import magmaquiz.composeapp.generated.resources.create_room
import magmaquiz.composeapp.generated.resources.room_name
import magmaquiz.composeapp.generated.resources.room_name_length
import magmaquiz.composeapp.generated.resources.set_room_details
import magmaquiz.composeapp.generated.resources.time_to_answer
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun GameSettingsScreen(
    gameSettingsViewModel: GameSettingsViewModel = koinViewModel(),
    navigateToHostScreen: () -> Unit
) {
    val state by gameSettingsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(gameSettingsViewModel) {
        gameSettingsViewModel.event.collect { event ->
            when (event) {
                is NetworkEvent.Failure -> SnackbarController.onEvent(getString(Res.string.cant_create_room))
                NetworkEvent.Success -> navigateToHostScreen()
            }
        }
    }

    val minSeconds = 5
    val maxSeconds = 120
    
    val roomName = state.roomName
    val roomNameValid = roomName.trim().length in 3..30
    val canSubmit = roomNameValid && state.questionTime in minSeconds..maxSeconds

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .imePadding()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .widthIn(max = 640.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(Res.string.create_room),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(Res.string.set_room_details),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = roomName,
                    onValueChange = {
                        gameSettingsViewModel.onCommand(GameSettingsCommand.NameChanged(it))
                    },
                    label = { Text(stringResource(Res.string.room_name)) },
                    singleLine = true,
                    isError = roomName.isNotBlank() && !roomNameValid
                )

                if (roomName.isNotBlank() && !roomNameValid) {
                    Text(
                        text = stringResource(Res.string.room_name_length),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = stringResource(Res.string.time_to_answer, state.questionTime),
                    style = MaterialTheme.typography.titleMedium
                )

                Slider(
                    value = state.questionTime.toFloat(),
                    onValueChange = { seconds ->
                        gameSettingsViewModel.onCommand(
                            GameSettingsCommand.QuestionTimeChanged(seconds.roundToInt())
                        )
                    },
                    valueRange = minSeconds.toFloat()..maxSeconds.toFloat(),
                    steps = (maxSeconds - minSeconds) - 1
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10, 15, 20, 30, 45, 60).forEach { preset ->
                        AssistChip(
                            onClick = {
                                gameSettingsViewModel.onCommand(
                                    GameSettingsCommand.QuestionTimeChanged(preset)
                                )
                            },
                            label = { Text("${preset}s") }
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        gameSettingsViewModel.onCommand(
                            GameSettingsCommand.VisibilityChanged(!state.isPublic)
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Widoczność pokoju",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = if (state.isPublic) "Publiczny - każdy może dołączyć" else "Tylko dla znajomych",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = state.isPublic,
                            onCheckedChange = { checked ->
                                gameSettingsViewModel.onCommand(
                                    GameSettingsCommand.VisibilityChanged(checked)
                                )
                            }
                        )
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canSubmit,
                    onClick = { gameSettingsViewModel.onCommand(GameSettingsCommand.Submit) }
                ) {
                    Text(stringResource(Res.string.create_room))
                }
            }
        }
    }
}
