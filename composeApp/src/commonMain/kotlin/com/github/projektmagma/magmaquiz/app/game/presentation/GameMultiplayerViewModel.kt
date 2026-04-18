package com.github.projektmagma.magmaquiz.app.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.util.TimeConverter.toSeconds
import com.github.projektmagma.magmaquiz.app.core.util.Timer
import com.github.projektmagma.magmaquiz.app.game.data.WsEvent
import com.github.projektmagma.magmaquiz.app.game.data.repository.GameRepository
import com.github.projektmagma.magmaquiz.app.game.presentation.model.play.AnswerState
import com.github.projektmagma.magmaquiz.app.game.presentation.model.play.GameCommand
import com.github.projektmagma.magmaquiz.app.game.presentation.model.play.GameState
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.UserAnswer
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class GameMultiplayerViewModel(
    private val gameRepository: GameRepository,
    private val quizRepository: QuizRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    private val _room = gameRepository.roomSettings

    private val _questions = gameRepository.questions

    private val _correctQuestions = MutableStateFlow<Map<ForeignUser, Int>>(emptyMap())
    val correctQuestions = _correctQuestions.asStateFlow()
    private val countdownTimer = Timer(viewModelScope)

    init {
        updateGameState()
        viewModelScope.launch {
            gameRepository.getMessages().collect { message ->
                when (message) {
                    is WsEvent.Closed -> {}
                    is WsEvent.OutGoing -> {
                        when (message.message) {
                            is WebSocketMessages.OutgoingMessage.GameEnded -> {
                                _gameState.update {
                                    it.copy(
                                        isQuizFinished = true,
                                        totalQuestions = _questions.value.size
                                    )
                                }
                                checkCorrectness(message.message.userAnswerMap)
                            }

                            is WebSocketMessages.OutgoingMessage.NextQuestion -> {
                                updateGameState()
                            }

                            is WebSocketMessages.OutgoingMessage.UserAnswered -> {} // id usera
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    fun onCommand(command: GameCommand) {
        when (command) {
            is GameCommand.AnswerClicked -> {
                onAnswerSelected(command)
            }

            GameCommand.FinishGame -> {
                viewModelScope.launch {
                    delay(400)
                    _gameState.update { it.copy(score = 0, currentQuestionIndex = 0) }
                    val message = if (checkIsHost()) WebSocketMessages.IncomingMessage.CloseRoom else WebSocketMessages.IncomingMessage.Disconnect
                    sendMessage(message)
                }
            }
        }
    }

    fun sendMessage(message: WebSocketMessages.IncomingMessage) {
        viewModelScope.launch {
            gameRepository.sendMessage(message)
        }
    }

    private fun onAnswerSelected(cmd: GameCommand.AnswerClicked) {
        if (_gameState.value.isAnswered) return

        val correctAnswer = _gameState.value.answers.find { it.content == cmd.content }
        val isCorrect = cmd.isCorrect ?: correctAnswer?.isCorrect

        _gameState.update { gameState ->
            gameState.copy(
                score = if (isCorrect == true) gameState.score + 1 else gameState.score,
                isAnswered = true,
                answers = gameState.answers.map {
                    it.copy(isSelected = it.content == cmd.content)
                }
            )
        }

        if (correctAnswer != null) {
            sendMessage(WebSocketMessages.IncomingMessage.Answer(correctAnswer.id))
        }
    }

    private fun updateGameState() {
        startCountdown()
        val question = _questions.value[_gameState.value.currentQuestionIndex]
        _gameState.update { gameState ->
            gameState.copy(
                questionContent = question.questionContent,
                questionNumber = question.questionNumber,
                questionImage = question.questionImage,
                currentQuestionIndex = gameState.currentQuestionIndex + 1,
                answers = question.answerList.map {
                    AnswerState(
                        it.id!!,
                        it.answerContent,
                        it.isCorrect
                    )
                },
                isAnswered = false,
                isQuizFinished = false,
                totalQuestions = quizRepository.quiz.value?.questionList?.size ?: 0
            )
        }
    }

    private fun startCountdown() {
        val seconds = _room.value!!.questionTimeInMillis.toSeconds()
        countdownTimer.start(
            from = seconds,
            onTick = { tick ->
                _gameState.update { it.copy(remainingSeconds = tick) }
            }
        )
    }

    private fun checkCorrectness(userAnswerMap: Map<UUID, List<UserAnswer>>) {
        viewModelScope.launch {
            val map = _room.value?.userList
                ?.mapNotNull { it.userId }
                ?.associateWith { 0 }
                ?.toMutableMap()
                ?: mutableMapOf()

            userAnswerMap.forEach { (uUID, answers) ->
                val question = _questions.value.firstOrNull { it.id == uUID } ?: return@forEach
                answers.forEach { answer ->
                    if (question.answerList.firstOrNull { it.isCorrect }?.id == answer.answerId) {
                        map[answer.userId] = (map[answer.userId] ?: 0) + 1
                    }
                }
            }
                
            _correctQuestions.value = map
                .mapNotNull { (userId, points) -> 
                    _room.value?.userList
                        ?.firstOrNull { it.userId == userId }
                        ?.let { user -> user to points }
                }.toMap()
        }
    }

    fun checkIsHost(): Boolean {
        return authRepository.thisUser.value?.userId == gameRepository.roomSettings.value?.roomOwner?.userId
    }
}