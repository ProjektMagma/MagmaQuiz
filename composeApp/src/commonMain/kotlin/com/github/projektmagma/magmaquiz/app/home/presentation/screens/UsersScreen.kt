package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.home.presentation.UsersViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.AutoScalableLazyColumn
import com.github.projektmagma.magmaquiz.app.home.presentation.components.SearchTextField
import com.github.projektmagma.magmaquiz.app.home.presentation.components.UserCard
import com.github.projektmagma.magmaquiz.app.home.presentation.model.users.UsersCommand
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UsersScreen() {

    val usersViewModel: UsersViewModel = koinViewModel()
    val userList by usersViewModel.userList.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchTextField(
            searchedText = usersViewModel.userName,
            labelText = "Username",
            onSearch = {
                usersViewModel.onCommand(UsersCommand.UserList(false))
            },
            onValueChange = {
                usersViewModel.userName = it
                usersViewModel.onCommand(UsersCommand.UserList(true))
            }
        )

        AutoScalableLazyColumn(userList) { user ->
            UserCard(user, navigateToUserDetails = {}) // TODO: Szczegóły użytkownika
        }
    }
}


