package com.github.projektmagma.magmaquiz.app.users.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FilterButton
import com.github.projektmagma.magmaquiz.app.core.presentation.components.SearchTextField
import com.github.projektmagma.magmaquiz.app.users.presentation.UsersListViewModel
import com.github.projektmagma.magmaquiz.app.users.presentation.model.list.UsersCommand
import com.github.projektmagma.magmaquiz.app.users.presentation.model.list.UsersFilters
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.friends
import magmaquiz.composeapp.generated.resources.incoming
import magmaquiz.composeapp.generated.resources.outgoing
import magmaquiz.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserSearchFieldAndFilters(modifier: Modifier, usersListViewModel: UsersListViewModel) {
    val state by usersListViewModel.state.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxWidth()) {
        SearchTextField(
            modifier = Modifier.fillMaxWidth(),
            searchedText = state.username,
            labelText = stringResource(Res.string.username),
            onSearch = {
                usersListViewModel.onCommand(UsersCommand.GetUserList(false))
            },
            onValueChange = {
                usersListViewModel.onCommand(UsersCommand.UsernameChanged(it))
                usersListViewModel.onCommand(UsersCommand.GetUserList(true))
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
                    contentLabel = stringResource(Res.string.friends),
                    contentIcon = Icons.Default.Group
                )
            }

            item {
                FilterButton(
                    selected = state.usersFilter == UsersFilters.IncomingInvitations,
                    onClick = {
                        usersListViewModel.onCommand(UsersCommand.FilterChanged(UsersFilters.IncomingInvitations))
                    },
                    contentLabel = stringResource(Res.string.incoming),
                    contentIcon = Icons.Filled.PersonAdd
                )
            }

            item {
                FilterButton(
                    selected = state.usersFilter == UsersFilters.SentInvitations,
                    onClick = {
                        usersListViewModel.onCommand(UsersCommand.FilterChanged(UsersFilters.SentInvitations))
                    },
                    contentLabel = stringResource(Res.string.outgoing),
                    contentIcon = Icons.Filled.Mail
                )
            }
        }
    }
}