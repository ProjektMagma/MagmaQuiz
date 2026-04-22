package com.github.projektmagma.magmaquiz.app.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.TimeConverter.toMillis
import com.github.projektmagma.magmaquiz.app.game.data.repository.GameRepository
import com.github.projektmagma.magmaquiz.app.game.presentation.model.settings.GameSettingsCommand
import com.github.projektmagma.magmaquiz.app.game.presentation.model.settings.GameSettingsState
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameSettingsViewModel(
    private val gameRepository: GameRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val _state = MutableStateFlow(GameSettingsState())
    val state = _state.asStateFlow()

    private val _event = Channel<NetworkEvent>()
    val event = _event.receiveAsFlow()

    fun onCommand(command: GameSettingsCommand) {
        when (command) {
            is GameSettingsCommand.NameChanged -> if (command.newName.length < 30) _state.update { it.copy(roomName = command.newName) }
            is GameSettingsCommand.QuestionTimeChanged -> _state.update { it.copy(questionTime = command.newTime) }
            is GameSettingsCommand.VisibilityChanged -> _state.update { it.copy(isPublic = command.visibility) }
            GameSettingsCommand.AddToHistory -> addQuizToHistory()
            GameSettingsCommand.Submit -> createRoom()
        }
    }

    private fun createRoom() {
        viewModelScope.launch {
            gameRepository.createGameRoom(
                roomName = _state.value.roomName,
                id = quizRepository.quiz.value?.id!!,
                isPublic = _state.value.isPublic,
                questionTimeMillis = _state.value.questionTime.toMillis()
            ).whenSuccess { createdRoom ->
                gameRepository.roomSettings.value = createdRoom.data

                gameRepository.joinRoom(createdRoom.data.roomId)
                    .whenSuccess {
                        _event.trySend(NetworkEvent.Success)
                    }
                    .whenError {
                        _event.trySend(NetworkEvent.Failure(it.error))
                    }
            }.whenError {
                _event.trySend(NetworkEvent.Failure(it.error))
            }
        }
    }
    
    private fun addQuizToHistory(){
        viewModelScope.launch { 
            quizRepository.markQuizAsPlayed()
        }
    }
}