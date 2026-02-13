package com.github.projektmagma.magmaquiz.app.users.presentation.screens

import androidx.compose.foundation.layout.*
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
import com.github.projektmagma.magmaquiz.app.core.presentation.components.AutoScalableLazyColumn
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FilterButton
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeErrorIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.components.SearchTextField
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.users.presentation.UsersListViewModel
import com.github.projektmagma.magmaquiz.app.users.presentation.UsersSharedViewModel
import com.github.projektmagma.magmaquiz.app.users.presentation.components.UserCard
import com.github.projektmagma.magmaquiz.app.users.presentation.model.list.UsersCommand
import com.github.projektmagma.magmaquiz.app.users.presentation.model.list.UsersFilters
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun UsersScreen(
    navigateToUserDetails: (id: UUID) -> Unit
) {
    val usersListViewModel: UsersListViewModel = koinViewModel()
    val usersSharedViewModel: UsersSharedViewModel = koinViewModel()
    val state by usersListViewModel.state.collectAsStateWithLifecycle()
    val uiState by usersListViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchTextField(
            modifier = Modifier
                .fillMaxWidth(),
            searchedText = state.username,
            labelText = stringResource(Res.string.username),
            onSearch = {
                usersListViewModel.onCommand(UsersCommand.UserList(false))
            },
            onValueChange = {
                usersListViewModel.onCommand(UsersCommand.UsernameChanged(it))
                usersListViewModel.onCommand(UsersCommand.UserList(true))
            }
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterButton(
                    selected = state.usersFilter == UsersFilters.Friends,
                    onClick = {
                        usersListViewModel.onCommand(UsersCommand.FilterChanged(UsersFilters.Friends))
                    },
                    contentLabel = "Friends",
                    contentIcon = Icons.Default.Group
                )
            }

            item {
                FilterButton(
                    selected = state.usersFilter == UsersFilters.SentInvitations,
                    onClick = {
                        usersListViewModel.onCommand(UsersCommand.FilterChanged(UsersFilters.SentInvitations))
                    },
                    contentLabel = "Sent invitations",
                    contentIcon = Icons.Filled.Mail
                )
            }

            item {
                FilterButton(
                    selected = state.usersFilter == UsersFilters.IncomingInvitations,
                    onClick = {
                        usersListViewModel.onCommand(UsersCommand.FilterChanged(UsersFilters.IncomingInvitations))
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
                    usersListViewModel.onCommand(UsersCommand.UserList(false))
                })

            UiState.Loading -> FullSizeCircularProgressIndicator()
            UiState.Success ->
                AutoScalableLazyColumn(state.usersList, key = { it.userId!! }) { user ->
                    UserCard(
                        user, 
                        usersSharedViewModel,
                        navigateToUserDetails = { navigateToUserDetails(it) }
                    )  
                }
        }

    }
}


