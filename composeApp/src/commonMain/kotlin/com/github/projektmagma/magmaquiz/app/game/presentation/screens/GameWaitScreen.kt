package com.github.projektmagma.magmaquiz.app.game.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.core.util.TimeConverter.toSeconds
import com.github.projektmagma.magmaquiz.app.game.presentation.GameWaitViewModel
import com.github.projektmagma.magmaquiz.app.game.presentation.model.GameEvent
import com.github.projektmagma.magmaquiz.app.game.presentation.model.wait.GameWaitCommand
import com.github.projektmagma.magmaquiz.app.game.presentation.model.wait.GameWaitState
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.RoomSettings
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.close_room
import magmaquiz.composeapp.generated.resources.in_progress
import magmaquiz.composeapp.generated.resources.leave_room
import magmaquiz.composeapp.generated.resources.nobody_joined
import magmaquiz.composeapp.generated.resources.players
import magmaquiz.composeapp.generated.resources.players_count
import magmaquiz.composeapp.generated.resources.seconds_per_question
import magmaquiz.composeapp.generated.resources.start_game
import magmaquiz.composeapp.generated.resources.waiting
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
// todo poprawic screen i content architekture
@Composable
fun GameWaitScreen(
    gameWaitViewModel: GameWaitViewModel = koinViewModel(),
    onStartGame: () -> Unit,
    onLeaveRoom: () -> Unit,
) {
    val room by gameWaitViewModel.roomSettings.collectAsStateWithLifecycle()
    val state by gameWaitViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        gameWaitViewModel.event.collect { 
            when (it) {
                is GameEvent.Closed -> onLeaveRoom()
                GameEvent.Success -> onStartGame()
            }
        }
    }

    when (val roomState = room) {
        null -> FullSizeCircularProgressIndicator()
        else -> GameHostContent(
            room = roomState,
            state = state,
            onLeaveRoom,
            gameWaitViewModel = gameWaitViewModel
        )
    }
}

@Composable
private fun GameHostContent(
    room: RoomSettings,
    state: GameWaitState,
    onLeaveRoom: () -> Unit,
    gameWaitViewModel: GameWaitViewModel
) {
    val timeSeconds = room.questionTimeInMillis.toSeconds()
    val isHost = gameWaitViewModel.checkIsHost()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .widthIn(max = 900.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (state.isVisibleDialog) {
            AlertDialog(
                confirmButton = { Button(
                    onClick = {
                        onLeaveRoom()   
                    }
                ) {
                    Text("OK")
                } },
                onDismissRequest = { gameWaitViewModel.onCommand(GameWaitCommand.DialogVisibilityChanged(false)) },
                text = { Text(state.errorMessage ?: "") }
            )
        }
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = room.roomName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(stringResource(Res.string.players_count, room.connectedUsers)) },
                        leadingIcon = { Icon(Icons.Outlined.Group, contentDescription = null) }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(stringResource(Res.string.seconds_per_question, timeSeconds)) },
                        leadingIcon = { Icon(Icons.Outlined.Schedule, contentDescription = null) }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(if (room.isInProgress) stringResource(Res.string.in_progress) else stringResource(Res.string.waiting)) },
                        leadingIcon = { Icon(Icons.Outlined.SportsEsports, contentDescription = null) }
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContentImage(
                    imageData = room.currentQuiz.quizImage,
                    imageSize = 90.dp
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = room.currentQuiz.quizName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Host: ${room.roomOwner.userName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.players),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                HorizontalDivider()

                if (room.userList.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.nobody_joined),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = room.userList,
                            key = { it.userId ?: it.userName }
                        ) { user ->
                            UserRow(
                                user = user,
                                isHost = user.userId == room.roomOwner.userId
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (isHost) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        gameWaitViewModel.sendMessage(WebSocketMessages.IncomingMessage.CloseRoom)
                    }
                ) {
                    Text(stringResource(Res.string.close_room))
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        gameWaitViewModel.sendMessage(WebSocketMessages.IncomingMessage.StartGame)
                    },
                    enabled = room.userList.isNotEmpty() && !room.isInProgress
                ) {
                    Text(stringResource(Res.string.start_game))
                }
            } else {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        gameWaitViewModel.sendMessage(WebSocketMessages.IncomingMessage.Disconnect)
                        gameWaitViewModel.leaveRoom()
                    }
                ) {
                    Text(stringResource(Res.string.leave_room))
                }
            }
        }
    }
}

@Composable
private fun UserRow(
    user: ForeignUser,
    isHost: Boolean
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfilePictureIcon(
                imageData = user.userProfilePicture,
                size = 36.dp
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.userName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (isHost) {
                AssistChip(
                    onClick = {},
                    label = { Text("HOST") }
                )
            }
        }
    }
}