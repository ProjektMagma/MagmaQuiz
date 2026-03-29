package com.github.projektmagma.magmaquiz.app.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.game.presentation.model.AnswerState
import com.github.projektmagma.magmaquiz.app.game.presentation.model.GameCommand
import com.github.projektmagma.magmaquiz.app.game.presentation.model.GameState
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameQuizViewModel(
    quizRepository: QuizRepository
) : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()
    
    private val _quiz = quizRepository.quiz
    val quiz = _quiz.asStateFlow()

    fun onCommand(command: GameCommand) {
        when (command) {
            is GameCommand.AnswerClicked -> onAnswerSelected(command)
            GameCommand.StartGame -> startGame()
            GameCommand.FinishGame -> {
                viewModelScope.launch { 
                    delay(400)
                    _gameState.update { it.copy(score = 0, currentQuestionIndex = 0) }
                }
            }
        }
    }

    private fun startGame() {
        updateGameState()
    }

    private fun onAnswerSelected(cmd: GameCommand.AnswerClicked) {
        if (_gameState.value.isAnswered) return

        val isCorrect = cmd.isCorrect ?: _gameState.value.answers.find { it.content == cmd.content }?.isCorrect
        if (isCorrect == true) {
            _gameState.update { gameState ->
                gameState.copy(score = gameState.score + 1)
            }
        }

        _gameState.update { gameState ->
            gameState.copy(
                isAnswered = true,
                answers = gameState.answers.map {
                    it.copy(isSelected = it.content == cmd.content)
                }
            )
        }

        viewModelScope.launch {
            delay(2000)
            _gameState.update { gameState ->
                gameState.copy(currentQuestionIndex = gameState.currentQuestionIndex + 1)
            }
            updateGameState()
        }
    }

    private fun updateGameState() {
        val questions = _quiz.value?.questionList ?: emptyList()
        if (_gameState.value.currentQuestionIndex >= questions.size) {
            _gameState.update { gameState ->
                gameState.copy(
                    isQuizFinished = true
                )
            }
        } else {
            val question = questions[_gameState.value.currentQuestionIndex]
            _gameState.update { gameState ->
                gameState.copy(
                    questionContent = question.questionContent,
                    questionNumber = question.questionNumber,
                    questionImage = question.questionImage,
                    answers = question.answerList.map { AnswerState(it.answerContent, it.isCorrect) },
                    isAnswered = false,
                    isQuizFinished = false,
                    totalQuestions = questions.size
                )
            }
        }
    }
}