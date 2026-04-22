package com.github.projektmagma.magmaquiz.app.quizzes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.details.QuizDetailsCommand
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.details.QuizDetailsState
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class QuizDetailsViewModel(
    private val id: UUID,
    private val quizRepository: QuizRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private val _quiz = MutableStateFlow<Quiz?>(null)
    private val _cachedQuizzes = quizRepository.cachedQuizzes
    
    val state = combine(
        _quiz,
        _cachedQuizzes
    ) { quiz, quizzes ->
        QuizDetailsState(
            quiz = quiz,
            quizzes = quizzes
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        QuizDetailsState()
    )

    init {
        if (_quiz.value?.id != id) {
            getQuizById()
        }
    }
    
    fun onCommand(command: QuizDetailsCommand){
        when (command){
            QuizDetailsCommand.AddQuizToHistory -> addQuizToMyHistory()
            QuizDetailsCommand.ChangeFavoriteStatus -> changeFavoriteStatus()
            QuizDetailsCommand.GetQuizById -> getQuizById()
            QuizDetailsCommand.SetupQuizForGame -> {
                quizRepository.quiz.value = _quiz.value
            }
        }
    }
    
    private fun getQuizById() {
        viewModelScope.launch {
            _quiz.value = null
            quizRepository.getQuizById(id).whenSuccess { result ->
                _quiz.value = result.data
                _cachedQuizzes.update { it.plus(result.data) }
                _uiState.value = UiState.Success
            }.whenError {
                _uiState.value = UiState.Error(it.error.toResId())
            }
        }
    }

    private fun changeFavoriteStatus() {
        viewModelScope.launch {
            quizRepository.changeFavoriteStatus(id)
        }
    }
    
    private fun addQuizToMyHistory(){
        viewModelScope.launch {
            quizRepository.markQuizAsPlayed()
        }
    }
}