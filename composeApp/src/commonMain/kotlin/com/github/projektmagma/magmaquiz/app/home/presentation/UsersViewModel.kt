package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.home.data.UsersRepository
import com.github.projektmagma.magmaquiz.app.home.presentation.model.users.UsersCommand
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class UsersViewModel(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _authChannel = Channel<NetworkEvent>()
    val authChannel = _authChannel.receiveAsFlow()

    var userName by mutableStateOf("")

    val userList = usersRepository.userList

    val friendList = usersRepository.friendList

    var searchLock = false


    init {
        onCommand(UsersCommand.UserList(false))
    }

    fun onCommand(command: UsersCommand) {
        when (command) {
            is UsersCommand.UserList -> userList(command.withDelay)
            is UsersCommand.UserDetails -> TODO()
            is UsersCommand.SendFriendInvite -> TODO()
            is UsersCommand.AcceptFriendInvite -> TODO()
        }
    }

    private fun userList(withDelay: Boolean = false) {
        viewModelScope.launch {
            if (searchLock && withDelay) return@launch
            searchLock = true
            withSearchDelay(withDelay) {
                when (val result = usersRepository.getFindUsersByName(userName)) {
                    is Resource.Error -> {
                        _authChannel.trySend(NetworkEvent.Failure(result.error))
                    }

                    is Resource.Success -> {
                        _authChannel.trySend(NetworkEvent.Success)
                    }
                }
                searchLock = false
            }
        }
    }
}