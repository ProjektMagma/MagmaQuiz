package com.github.projektmagma.magmaquiz.app.users.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.util.Paginator
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.users.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.app.users.presentation.model.list.UsersCommand
import com.github.projektmagma.magmaquiz.app.users.presentation.model.list.UsersFilters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsersListViewModel(
    private val usersRepository: UsersRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private val _state = usersRepository.usersState
    val state = _state.asStateFlow()

    val paginator = Paginator(
        initialKey = 0,
        onLoadUpdated = { isLoading ->
            _state.update { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { currentPage ->
            val username = _state.value.username
            when (_state.value.usersFilter) {
                UsersFilters.Friends -> usersRepository.getFriendList(username, offset = currentPage)
                UsersFilters.SentInvitations -> usersRepository.getOutgoingInvitations(username, offset = currentPage)
                UsersFilters.IncomingInvitations -> usersRepository.getIncomingInvitations(username, offset = currentPage)
                UsersFilters.None -> usersRepository.getFindUsers(username, offset = currentPage)
            }
        },
        getNextKey = { currentPage, _ ->
            currentPage + 1
        },
        onError = { networkError ->
            _uiState.value = UiState.Error(networkError.toResId())
        },
        onSuccess = { items, _ ->
            _state.update { state ->
                state.copy(
                    usersList = state.usersList + items
                )
            }
            _uiState.value = UiState.Success
        },
        endReached = { _, items ->
            items.isEmpty()
        }
    )
    
    var searchLock = false
    
    init {
        loadNextItems()
    }

    fun onCommand(command: UsersCommand) {
        when (command) {
            is UsersCommand.GetUserList -> getNewUserList(command.withDelay)
            is UsersCommand.FilterChanged -> {
                _uiState.value = UiState.Loading
                _state.update {
                    it.copy(
                        usersFilter = if (command.newFilter != _state.value.usersFilter) 
                            command.newFilter 
                        else UsersFilters.None
                    )
                }
                getNewUserList(false)
            }
            is UsersCommand.UsernameChanged -> _state.update { it.copy(username = command.newUsername) }
            UsersCommand.LoadMore -> loadNextItems()
        }
    }
    
    private fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun getNewUserList(withDelay: Boolean = false) {
        viewModelScope.launch {
            if (searchLock && withDelay) return@launch
            searchLock = true
            withSearchDelay(withDelay) {
                paginator.reset()
                _state.update { it.copy(usersList = emptyList()) }
                paginator.loadNextItems()
                searchLock = false
            }
        }
    }
}