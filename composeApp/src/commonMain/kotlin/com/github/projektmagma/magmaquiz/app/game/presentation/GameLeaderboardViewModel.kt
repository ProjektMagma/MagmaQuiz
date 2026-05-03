package com.github.projektmagma.magmaquiz.app.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.game.data.WsEvent
import com.github.projektmagma.magmaquiz.app.game.data.repository.GameRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class GameLeaderboardViewModel(
    private val gameRepository: GameRepository,
) : ViewModel() {
    private val _room = gameRepository.roomSettings
    val room = _room.asStateFlow()

    private val _scores = MutableStateFlow<Map<ForeignUser, List<UUID?>>>(emptyMap())
    val scores = _scores.asStateFlow()

    private val _isGameEnded = MutableStateFlow(false)
    val isGameEnded = _isGameEnded.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion = _currentQuestion.asStateFlow()
    
    init {
        _scores.value = _room.value?.userList?.associateWith { emptyList() } ?: emptyMap()
        _currentQuestion.value = _room.value?.currentQuiz?.questionList?.firstOrNull()
        collectMessages()
    }

    private fun collectMessages() {
        var currentIndex = 0
        viewModelScope.launch {
            gameRepository.messages.collect { event ->
                when (event) {
                    is WsEvent.Closed -> {}
                    is WsEvent.OutGoing -> when (event.message) {
                        is WebSocketMessages.OutgoingMessage.NextQuestion -> {
                            currentIndex += 1
                            _currentQuestion.update { event.message.question }
                        }

                        is WebSocketMessages.OutgoingMessage.UserAnswered -> {
                            val room = _room.value ?: return@collect
                            val user = room.userList.firstOrNull { it.userId == event.message.userId } ?: return@collect

                            if (_room.value!!.currentQuiz.questionList[currentIndex].answerList.find { it.id == event.message.answerId }?.isCorrect == true) {
                                _scores.update { current ->
                                    val updated = current.toMutableMap()
                                    updated[user] = mutableListOf<UUID?>().apply {
                                        addAll(updated[user] ?: emptyList())
                                        add(event.message.answerId)
                                    }
                                    updated
                                }
                            }
                        }

                        is WebSocketMessages.OutgoingMessage.UserJoined -> {
                            _scores.update { current ->
                                val updated = current.toMutableMap()
                                if (!updated.containsKey(event.message.user)) updated[event.message.user] = emptyList()
                                updated
                            }
                        }

                        is WebSocketMessages.OutgoingMessage.UserLeft -> {
                            _scores.update { current ->
                                current.filterKeys { it.userId != event.message.userId }
                            }
                        }

                        is WebSocketMessages.OutgoingMessage.GameEnded -> {
                            _isGameEnded.value = true
                            _currentQuestion.value = null
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}