package com.github.projektmagma.magmaquiz.app.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.game.data.WsEvent
import com.github.projektmagma.magmaquiz.app.game.data.repository.GameRepository
import com.github.projektmagma.magmaquiz.app.game.presentation.model.GameEvent
import com.github.projektmagma.magmaquiz.app.game.presentation.model.wait.GameWaitCommand
import com.github.projektmagma.magmaquiz.app.game.presentation.model.wait.GameWaitState
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameWaitViewModel(
    private val gameRepository: GameRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _roomSettings = gameRepository.roomSettings
    val roomSettings = _roomSettings.asStateFlow()
    
    private val _state = MutableStateFlow(GameWaitState())
    val state = _state.asStateFlow()
    
    private val _event = Channel<GameEvent>()
    val event = _event.receiveAsFlow()

    init {
        viewModelScope.launch { 
            gameRepository.messages.collect { message ->
                when (message) {
                    is WsEvent.Closed -> if (!checkIsHost() && !_state.value.isUserInitializeLeave) 
                        _state.update { it.copy(errorMessage = message.reason, isVisibleDialog = true) } 
                    else 
                        _event.send(GameEvent.Closed(message.reason))
                    is WsEvent.OutGoing -> when (message.message) {
                        is WebSocketMessages.OutgoingMessage.NextQuestion -> {
                            _event.trySend(GameEvent.Success)
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
    
    fun onCommand(command: GameWaitCommand) {
        when (command) {
            is GameWaitCommand.DialogVisibilityChanged -> _state.update { it.copy(isVisibleDialog = command.visibility) }
        }
    }
    
    fun leaveRoom() {
        _state.update { it.copy(isUserInitializeLeave = true) }
    }
    
    fun sendMessage(message: WebSocketMessages.IncomingMessage){
        viewModelScope.launch { 
            gameRepository.sendMessage(message)
        }
    }
    
    fun checkIsHost(): Boolean {
        return authRepository.thisUser.value?.userId == roomSettings.value?.roomOwner?.userId
    }
}