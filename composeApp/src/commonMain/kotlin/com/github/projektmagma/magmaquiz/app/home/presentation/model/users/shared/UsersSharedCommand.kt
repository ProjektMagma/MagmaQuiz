package com.github.projektmagma.magmaquiz.app.home.presentation.model.users.shared

import java.util.UUID

sealed interface UsersSharedCommand {
    data class SendFriendInvite(val uuid: UUID) : UsersSharedCommand
    data class AcceptFriendInvite(val uuid: UUID) : UsersSharedCommand
    data class CancelFriendInvite(val uuid: UUID) : UsersSharedCommand
    data class DeleteFriend(val uuid: UUID) : UsersSharedCommand
}