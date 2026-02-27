package com.github.projektmagma.magmaquiz.app.users.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.users.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.app.users.presentation.model.list.UsersCommand
import com.github.projektmagma.magmaquiz.app.users.presentation.model.list.UsersFilters
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsersListViewModel(
    private val usersRepository: UsersRepository
) : ViewModel() {
    private val _authChannel = Channel<NetworkEvent>()
    val authChannel = _authChannel.receiveAsFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private val _state = usersRepository.usersState
    val state = _state.asStateFlow()

    var searchLock = false


    init {
        onCommand(UsersCommand.UserList(false))
    }

    fun onCommand(command: UsersCommand) {
        when (command) {
            is UsersCommand.UserList -> userList(command.withDelay)
            is UsersCommand.FilterChanged -> {
                _uiState.value = UiState.Loading
                _state.update {
                    it.copy(
                        usersFilter = if (command.newFilter != _state.value.usersFilter) 
                            command.newFilter 
                        else UsersFilters.None
                    )
                }
                getFilteredData()
            }
            is UsersCommand.UsernameChanged -> _state.update { it.copy(username = command.newUsername) }
        }
    }
    
    private fun getFilteredData(){
        viewModelScope.launch {
            val username = _state.value.username
            when (_state.value.usersFilter) {
                UsersFilters.Friends -> {
                    usersRepository.getFriendList(username)
                        .whenSuccess { result ->
                            updateUserList(result.data)
                        }
                }
                UsersFilters.SentInvitations -> {
                    usersRepository.getOutgoingInvitations(username)
                        .whenSuccess { result ->
                            updateUserList(result.data)
                        }
                }
                UsersFilters.IncomingInvitations -> {
                    usersRepository.getIncomingInvitations(username)
                        .whenSuccess { result ->
                            updateUserList(result.data)
                        }
                }

                UsersFilters.None -> {
                    userList(false)
                }
            }   
        }
    }
    
    private fun updateUserList(data: List<ForeignUser>){
        _state.update { it.copy(usersList = data) }
        _uiState.value = UiState.Success
    }

    private fun userList(withDelay: Boolean = false) {
        viewModelScope.launch {
            if (searchLock && withDelay) return@launch
            _uiState.value = UiState.Loading
            searchLock = true
            withSearchDelay(withDelay) {
                usersRepository.getFindUsers(_state.value.username).whenSuccess { result ->
                    _state.update { it.copy(usersList = result.data) }
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