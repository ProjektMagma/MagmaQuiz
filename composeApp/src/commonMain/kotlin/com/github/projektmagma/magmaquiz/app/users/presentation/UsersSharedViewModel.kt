package com.github.projektmagma.magmaquiz.app.users.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.users.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.app.users.presentation.model.shared.UsersSharedCommand
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.FriendshipStatus
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class UsersSharedViewModel(
    private val usersRepository: UsersRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val _state = usersRepository.usersState
    val state = _state.asStateFlow()
    
    private val _user = usersRepository.user
    
    fun onCommand(command: UsersSharedCommand){
        when (command) {
            is UsersSharedCommand.SendFriendInvite -> {
                sendInvite(command.uuid, FriendshipStatus.Outgoing)
            }
            is UsersSharedCommand.AcceptFriendInvite -> {
                acceptInvite(command.uuid)
                removeIncomingFriend(command.uuid)
            }
            is UsersSharedCommand.CancelFriendInvite -> {
                sendInvite(command.uuid, FriendshipStatus.None)
                removeIncomingFriend(command.uuid)
            }
            is UsersSharedCommand.DeleteFriend -> {
                sendInvite(command.uuid, FriendshipStatus.None)
            }
        }
    }
    
    private fun removeIncomingFriend(id: UUID){
        quizRepository.homeState.update {
            it.copy(
                incomingFriends = it.incomingFriends.filter { user -> user.userId != id }
            )
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