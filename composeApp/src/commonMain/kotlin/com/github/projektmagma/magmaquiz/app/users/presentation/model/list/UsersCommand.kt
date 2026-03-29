package com.github.projektmagma.magmaquiz.app.users.presentation.model.list

sealed interface UsersCommand {
    data class UsernameChanged(val newUsername: String): UsersCommand
    data class FilterChanged(val newFilter: UsersFilters): UsersCommand
    data class GetUserList(val withDelay: Boolean) : UsersCommand
    data object LoadMore: UsersCommand
}