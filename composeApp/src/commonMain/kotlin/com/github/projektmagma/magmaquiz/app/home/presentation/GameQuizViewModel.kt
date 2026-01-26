package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.home.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.home.presentation.model.game.AnswerState
import com.github.projektmagma.magmaquiz.app.home.presentation.model.game.GameCommand
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameQuizViewModel(
    quizRepository: QuizRepository
) : ViewModel() {

    private val _gameState = quizRepository.gameState
    val gameState = _gameState.asStateFlow()
    
    private val _quiz = quizRepository.quiz
    val quiz = _quiz.asStateFlow()

    fun onCommand(command: GameCommand) {
        when (command) {
            is GameCommand.AnswerClicked -> onAnswerSelected(command)
            GameCommand.StartGame -> startGame()
        }
    }

    private fun startGame() {
        _gameState.update { it.copy(currentQuestionIndex = 0) }
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
                gameState.copy(isQuizFinished = true)
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