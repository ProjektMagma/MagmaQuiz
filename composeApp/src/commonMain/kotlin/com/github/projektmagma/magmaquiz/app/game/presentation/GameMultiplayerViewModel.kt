package com.github.projektmagma.magmaquiz.app.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.util.TimeConverter.toSeconds
import com.github.projektmagma.magmaquiz.app.core.util.Timer
import com.github.projektmagma.magmaquiz.app.game.data.WsEvent
import com.github.projektmagma.magmaquiz.app.game.data.repository.GameRepository
import com.github.projektmagma.magmaquiz.app.game.presentation.model.GameEvent
import com.github.projektmagma.magmaquiz.app.game.presentation.model.play.AnswerState
import com.github.projektmagma.magmaquiz.app.game.presentation.model.play.GameCommand
import com.github.projektmagma.magmaquiz.app.game.presentation.model.play.GameState
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class GameMultiplayerViewModel(
    private val gameRepository: GameRepository,
    private val quizRepository: QuizRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _room = gameRepository.roomSettings
    val roomSettings = _room.asStateFlow()

    private val _gameState = MutableStateFlow(GameState(
        secondsForQuestion = _room.value!!.questionTimeInMillis.toSeconds()
    ))
    val gameState = _gameState.asStateFlow()
    
    private val _selectedAnswers = MutableStateFlow<Map<UUID, String>>(emptyMap())
    val selectedAnswers = _selectedAnswers.asStateFlow()

    private val _event = Channel<GameEvent>()
    val event = _event.receiveAsFlow()

    private val countdownTimer = Timer(viewModelScope)
    private val _questions = _room.value!!.currentQuiz.questionList

    init {
        updateGameState()
        viewModelScope.launch {
            gameRepository.messages.collect { message ->
                when (message) {
                    is WsEvent.Closed -> _event.send(GameEvent.Closed(message.reason))
                    is WsEvent.OutGoing -> {
                        when (message.message) {
                            is WebSocketMessages.OutgoingMessage.GameEnded -> {
                                _gameState.update {
                                    it.copy(
                                        isQuizFinished = true,
                                        totalQuestions = _questions.size
                                    )
                                }
                            }
                            is WebSocketMessages.OutgoingMessage.NextQuestion -> {
                                updateGameState()
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    fun onCommand(command: GameCommand) {
        when (command) {
            is GameCommand.AnswerClicked -> onAnswerSelected(command)
            GameCommand.FinishGame -> {
                viewModelScope.launch {
                    delay(400.milliseconds)
                    _gameState.update { it.copy(score = 0, currentQuestionIndex = 0) }
                    _selectedAnswers.update { emptyMap() }
                    val message = if (checkIsHost())
                        WebSocketMessages.IncomingMessage.CloseRoom
                    else
                        WebSocketMessages.IncomingMessage.Disconnect
                    sendMessage(message)
                }
            }
        }
    }

    fun checkIsHost(): Boolean {
        return _room.value?.roomOwner?.userId == authRepository.thisUser.value?.userId
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
        
        val currentQuestion = _questions.getOrNull(_gameState.value.currentQuestionIndex - 1)
        if (currentQuestion != null && correctAnswer != null) {
            _selectedAnswers.update { it + (currentQuestion.id!! to correctAnswer.content) }
        }

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
        val question = _questions[_gameState.value.currentQuestionIndex]
        _gameState.update { gameState ->
            gameState.copy(
                questionContent = question.questionContent,
                questionNumber = question.questionNumber,
                questionImage = question.questionImage,
                currentQuestionIndex = gameState.currentQuestionIndex + 1,
                answers = question.answerList.map {
                    AnswerState(it.id!!, it.answerContent, it.isCorrect)
                },
                isAnswered = false,
                isQuizFinished = false,
                totalQuestions = quizRepository.quiz.value?.questionList?.size ?: 0
            )
        }
    }

    private fun startCountdown() {
        countdownTimer.start(
            from = _gameState.value.secondsForQuestion,
            onTick = { tick ->
                _gameState.update { it.copy(remainingSeconds = tick) }
            }
        )
    }
}