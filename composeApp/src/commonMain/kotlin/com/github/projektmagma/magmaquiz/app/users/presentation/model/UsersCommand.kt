package com.github.projektmagma.magmaquiz.app.users.presentation.model

import java.util.*

sealed interface UsersCommand {
    data class UsernameChanged(val newUsername: String): UsersCommand
    data class FilterChanged(val newFilter: UsersFilters): UsersCommand
    data class UserList(val withDelay: Boolean) : UsersCommand
    data class SendFriendInvite(val uuid: UUID) : UsersCommand
    data class AcceptFriendInvite(val uuid: UUID) : UsersCommand

}