package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.home.presentation.UsersViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.*
import com.github.projektmagma.magmaquiz.app.home.presentation.model.users.UsersCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun UsersScreen(
    navigateToUserDetails: (id: UUID) -> Unit
) {
    val usersViewModel: UsersViewModel = koinViewModel()
    val userList by usersViewModel.userList.collectAsStateWithLifecycle()
    val uiState by usersViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            searchedText = usersViewModel.userName,
            labelText = stringResource(Res.string.username),
            onSearch = {
                usersViewModel.onCommand(UsersCommand.UserList(false))
            },
            onValueChange = {
                usersViewModel.userName = it
                usersViewModel.onCommand(UsersCommand.UserList(true))
            }
        )

        when (uiState) {
            is UiState.Error -> FullSizeErrorIndicator(
                message = (uiState as UiState.Error).errorMessage,
                onRetry = {
                    usersViewModel.onCommand(UsersCommand.UserList(false))
                })

            UiState.Loading -> FullSizeCircularProgressIndicator()
            UiState.Success ->
                AutoScalableLazyColumn(userList, key = { it.userId!! }) { user ->
                    UserCard(
                        user, 
                        navigateToUserDetails = { navigateToUserDetails(it) }
                    ) 
                }
        }

    }
}


