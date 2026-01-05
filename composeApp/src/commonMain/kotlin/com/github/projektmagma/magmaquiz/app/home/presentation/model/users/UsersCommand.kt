package com.github.projektmagma.magmaquiz.app.home.presentation.model.users

import java.util.*

sealed interface UsersCommand {
    data class UserList(val withDelay: Boolean) : UsersCommand
    data class UserDetails(val uuid: UUID) : UsersCommand
    data class SendFriendInvite(val uuid: UUID) : UsersCommand
    data class AcceptFriendInvite(val uuid: UUID) : UsersCommand

}