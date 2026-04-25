package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.AutoScalableLazyColumn
import com.github.projektmagma.magmaquiz.app.core.presentation.components.SearchTextField
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.home.presentation.RoomListViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.RoomCardSmall
import com.github.projektmagma.magmaquiz.app.home.presentation.model.rooms.RoomListCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.available_rooms
import magmaquiz.composeapp.generated.resources.current_games
import magmaquiz.composeapp.generated.resources.game_not_exist
import magmaquiz.composeapp.generated.resources.room_name
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RoomListScreen(
    roomListViewModel: RoomListViewModel = koinViewModel(),
    onJoinClick: () -> Unit
) {
    val state by roomListViewModel.state.collectAsStateWithLifecycle()
    val uiState by roomListViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        roomListViewModel.event.collect { event ->
            when (event) {
                is NetworkEvent.Failure -> SnackbarController.onEvent(getString(Res.string.game_not_exist))
                NetworkEvent.Success -> onJoinClick()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.wrapContentWidth()) {
                        Text(
                            text = stringResource(Res.string.current_games),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(Res.string.available_rooms, state.roomList.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { roomListViewModel.onCommand(RoomListCommand.RefreshRooms) },
                        enabled = !state.isRefreshing
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null
                        )
                    }
                }

            }

            Box(modifier = Modifier.weight(1f)) {
                AutoScalableLazyColumn(
                    itemList = state.roomList,
                    key = { it.roomId },
                    uiState = uiState,
                    isLoadingMore = state.isLoadingMoreRooms,
                    onLoadMore = { roomListViewModel.onCommand(RoomListCommand.LoadMore) },
                    stickyHeader = {
                        SearchTextField(
                            modifier = Modifier.fillMaxWidth(),
                            searchedText = state.stringToSearch,
                            labelText = stringResource(Res.string.room_name),
                            onSearch = {
                                roomListViewModel.onCommand(RoomListCommand.SearchByName(false))
                            },
                            onValueChange = {
                                roomListViewModel.onCommand(RoomListCommand.StringToSearchChanged(it))
                                roomListViewModel.onCommand(RoomListCommand.SearchByName(true))
                            }
                        )
                    },
                    content = { room ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            RoomCardSmall(
                                room = room,
                                onJoinClick = { roomId ->
                                    roomListViewModel.onCommand(RoomListCommand.JoinRoom(roomId))
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}
