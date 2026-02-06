package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.home.presentation.UsersViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.AutoScalableLazyColumn
import com.github.projektmagma.magmaquiz.app.home.presentation.components.FilterButton
import com.github.projektmagma.magmaquiz.app.home.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.home.presentation.components.FullSizeErrorIndicator
import com.github.projektmagma.magmaquiz.app.home.presentation.components.SearchTextField
import com.github.projektmagma.magmaquiz.app.home.presentation.components.UserCard
import com.github.projektmagma.magmaquiz.app.home.presentation.model.users.UsersCommand
import com.github.projektmagma.magmaquiz.app.home.presentation.model.users.UsersFilters
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.send_invite
import magmaquiz.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun UsersScreen(
    navigateToUserDetails: (id: UUID) -> Unit
) {
    val usersViewModel: UsersViewModel = koinViewModel()
    val state by usersViewModel.state.collectAsStateWithLifecycle()
    val uiState by usersViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            searchedText = state.username,
            labelText = stringResource(Res.string.username),
            onSearch = {
                usersViewModel.onCommand(UsersCommand.UserList(false))
            },
            onValueChange = {
                usersViewModel.onCommand(UsersCommand.UsernameChanged(it))
                usersViewModel.onCommand(UsersCommand.UserList(true))
            }
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterButton(
                    selected = state.usersFilter == UsersFilters.Friends,
                    onClick = {
                        usersViewModel.onCommand(UsersCommand.FilterChanged(UsersFilters.Friends))
                    },
                    contentLabel = "Friends",
                    contentIcon = Icons.Default.Group
                )
            }

            item {
                FilterButton(
                    selected = state.usersFilter == UsersFilters.SentInvitations,
                    onClick = {
                        usersViewModel.onCommand(UsersCommand.FilterChanged(UsersFilters.SentInvitations))
                    },
                    contentLabel = "Sent invitations",
                    contentIcon = Icons.Filled.Mail
                )
            }

            item {
                FilterButton(
                    selected = state.usersFilter == UsersFilters.IncomingInvitations,
                    onClick = {
                        usersViewModel.onCommand(UsersCommand.FilterChanged(UsersFilters.IncomingInvitations))
                    },
                    contentLabel = "Incoming invitations",
                    contentIcon = Icons.Filled.PersonAdd
                )
            }
        }

        when (uiState) {
            is UiState.Error -> FullSizeErrorIndicator(
                message = (uiState as UiState.Error).errorMessage,
                onRetry = {
                    usersViewModel.onCommand(UsersCommand.UserList(false))
                })

            UiState.Loading -> FullSizeCircularProgressIndicator()
            UiState.Success ->
                AutoScalableLazyColumn(state.usersList, key = { it.userId!! }) { user ->
                    UserCard(
                        user, 
                        navigateToUserDetails = { navigateToUserDetails(it) },
                        inviteButtonText = Res.string.send_invite,
                        onInviteButtonClick = { usersViewModel.onCommand(UsersCommand.SendFriendInvite(it)) }
                    )  
                }
        }

    }
}


