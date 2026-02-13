package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.home.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.app.home.presentation.model.users.shared.UsersSharedCommand
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.FriendshipStatus
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class UsersSharedViewModel(
    private val usersRepository: UsersRepository
) : ViewModel() {
    private val _state = usersRepository.usersState
    val state = _state.asStateFlow()
    
    private val _user = usersRepository.user
    val user = _user.asStateFlow()
    
    fun onCommand(command: UsersSharedCommand){
        when (command) {
            is UsersSharedCommand.SendFriendInvite -> {
                sendInvite(command.uuid, FriendshipStatus.Outgoing)
            }
            is UsersSharedCommand.AcceptFriendInvite -> {
                acceptInvite(command.uuid)
            }
            is UsersSharedCommand.CancelFriendInvite -> {
                sendInvite(command.uuid, FriendshipStatus.None)
            }
            is UsersSharedCommand.DeleteFriend -> {
                sendInvite(command.uuid, FriendshipStatus.None)
            }
        }
    }

    private fun sendInvite(uuid: UUID, friendshipStatus: FriendshipStatus){
        viewModelScope.launch {
            usersRepository.getSendFriendInvite(uuid)
                .whenSuccess {
                    changeUserFriendshipStatus(uuid, friendshipStatus)
                    _user.update { (it as? ForeignUser)?.copy(friendshipStatus = friendshipStatus) }
                }
        }
    }

    private fun acceptInvite(uuid: UUID){
        viewModelScope.launch {
            usersRepository.getAcceptFriendInvite(uuid)
                .whenSuccess { 
                    changeUserFriendshipStatus(uuid, FriendshipStatus.Friends)
                    _user.update { (it as? ForeignUser)?.copy(friendshipStatus = FriendshipStatus.Friends) }
                }
        }
    }

    private fun changeUserFriendshipStatus(uuid: UUID, friendshipStatus: FriendshipStatus){
        _state.update { state ->
            state.copy(
                usersList = _state.value.usersList.map {
                    if (it.userId == uuid) {
                        it.copy(friendshipStatus = friendshipStatus)
                    } else {
                        it
                    }
                }
            )
        }
    }
}