package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.home.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.app.home.presentation.model.users.UsersCommand
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class UsersViewModel(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _authChannel = Channel<NetworkEvent>()
    val authChannel = _authChannel.receiveAsFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

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
            _uiState.value = UiState.Loading
            searchLock = true
            withSearchDelay(withDelay) {
                usersRepository.getFindUsersByName(userName).whenSuccess {
                    _authChannel.trySend(NetworkEvent.Success)
                    _uiState.value = UiState.Success
                }.whenError {
                    _authChannel.trySend(NetworkEvent.Failure(it.error))
                    _uiState.value = UiState.Error(it.error.toResId())
                }
                searchLock = false
            }
        }
    }
}