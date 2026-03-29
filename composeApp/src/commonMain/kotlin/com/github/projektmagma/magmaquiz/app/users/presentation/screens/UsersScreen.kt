package com.github.projektmagma.magmaquiz.app.users.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.AutoScalableLazyColumn
import com.github.projektmagma.magmaquiz.app.users.presentation.UsersListViewModel
import com.github.projektmagma.magmaquiz.app.users.presentation.UsersSharedViewModel
import com.github.projektmagma.magmaquiz.app.users.presentation.components.UserCard
import com.github.projektmagma.magmaquiz.app.users.presentation.components.UserSearchFieldAndFilters
import com.github.projektmagma.magmaquiz.app.users.presentation.model.list.UsersCommand
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
        AutoScalableLazyColumn(
            itemList = state.usersList,
            key = { it.userId!! },
            uiState = uiState,
            isLoadingMore = state.isLoadingMore,
            onLoadMore = { usersListViewModel.onCommand(UsersCommand.LoadMore) },
            stickyHeader = {
                UserSearchFieldAndFilters(it, usersListViewModel)
            },
            content = { user ->
                UserCard(
                    user,
                    usersSharedViewModel,
                    navigateToUserDetails = { navigateToUserDetails(it) }
                )
            }
        )
    }
}