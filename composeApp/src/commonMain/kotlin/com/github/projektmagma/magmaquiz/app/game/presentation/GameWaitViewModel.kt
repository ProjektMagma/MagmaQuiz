package com.github.projektmagma.magmaquiz.app.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.LocalEvent
import com.github.projektmagma.magmaquiz.app.game.data.WsEvent
import com.github.projektmagma.magmaquiz.app.game.data.repository.GameRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages
import kotlinx.coroutines.channels.Channel
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
    
    private val _event = Channel<LocalEvent>()
    val event = _event.receiveAsFlow()

    init {
        viewModelScope.launch { 
            gameRepository.getMessages().collect { message ->
                when (message) {
                    is WsEvent.Closed -> _event.send(LocalEvent.Failure)
                    is WsEvent.OutGoing -> when (message.message) {
                        is WebSocketMessages.OutgoingMessage.SettingsChanged -> { _roomSettings.value = message.message.roomSettings }
                        is WebSocketMessages.OutgoingMessage.UserJoined -> {
                            _roomSettings.update { state ->
                                val updatedUsers = (state!!.userList.plus(message.message.user))
                                state.copy(
                                    userList = updatedUsers,
                                    connectedUsers = updatedUsers.size
                                )
                            }
                        }
                        is WebSocketMessages.OutgoingMessage.UserLeft -> {
                            _roomSettings.update { state ->
                                val updatedUsers = state!!.userList.filter { it.userId != message.message.userId }
                                state.copy(
                                    userList = updatedUsers,
                                    connectedUsers = updatedUsers.size
                                )
                            }
                        }
                        is WebSocketMessages.OutgoingMessage.NextQuestion -> {
                            gameRepository.questions.update { it.plus(message.message.question) }
                            _event.trySend(LocalEvent.Success)
                        }
                        else -> Unit
                    }
                }
            }
        }
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