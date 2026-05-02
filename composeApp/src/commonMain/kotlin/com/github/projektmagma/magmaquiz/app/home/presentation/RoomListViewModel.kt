package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.util.Paginator
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.game.data.repository.GameRepository
import com.github.projektmagma.magmaquiz.app.home.presentation.model.rooms.RoomListCommand
import com.github.projektmagma.magmaquiz.app.home.presentation.model.rooms.RoomListState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class RoomListViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RoomListState())
    val state = _state.asStateFlow()
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private val _event = Channel<NetworkEvent>()
    val event = _event.receiveAsFlow()
    
    val paginator = Paginator(
        initialKey = 0,
        onLoadUpdated = { isLoading ->
            _state.update { it.copy(isLoadingMoreRooms = isLoading) }
        },
        onRequest = { currentPage -> gameRepository.getRoomList(offset = currentPage) },
        getNextKey = { currentKey, _ -> currentKey + 1 },
        onError = { networkError -> _uiState.value = UiState.Error(networkError.toResId()) },
        onSuccess = { result, _ ->
            _state.update { it.copy(roomList = it.roomList + result) }
            _uiState.value = UiState.Success
        },
        endReached = { _, items -> items.isEmpty() }
    )
    
    init {
        loadNextRooms()
    }
    
    fun onCommand(command: RoomListCommand) {
        when (command) {
            RoomListCommand.LoadMore -> loadNextRooms()
            RoomListCommand.RefreshRooms -> refreshRooms()
            is RoomListCommand.JoinRoom -> joinRoom(command.id)
            is RoomListCommand.StringToSearchChanged -> _state.update { it.copy(stringToSearch = command.newString) }
            is RoomListCommand.SearchByName -> getRoomByName(command.withDelay)
        }
    }
    
    private fun joinRoom(id: UUID) {
        viewModelScope.launch {
            gameRepository.joinRoom(id)
                .whenSuccess { _event.send(NetworkEvent.Success) }
                .whenError { result ->
                    _state.update { 
                        it.copy(
                            roomList = it.roomList.filter { room -> room.roomId != id }
                        )
                    }
                    _event.send(NetworkEvent.Failure(result.error)) 
                }
        }
    }

    var searchLock = false
    private fun getRoomByName(withDelay: Boolean){
        viewModelScope.launch {
            if (searchLock && withDelay) return@launch
            searchLock = true
            _uiState.value = UiState.Loading
            withSearchDelay(withDelay) {
                paginator.reset()
                _state.update { it.copy(roomList = emptyList()) }
                paginator.loadNextItems()
                searchLock = false
            }
        }
    }

    private fun refreshRooms() {
        if (_state.value.isRefreshing) return

        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            try {
                _uiState.value = UiState.Loading
                paginator.reset()
                _state.update { it.copy(roomList = emptyList()) }
                paginator.loadNextItems()
            } finally {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun loadNextRooms(){
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }
}