package com.github.projektmagma.magmaquiz.app.quizzes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class QuizDetailsViewModel(
    private val id: UUID,
    private val quizRepository: QuizRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private val _quiz = quizRepository.quiz
    val quiz = _quiz.asStateFlow()

    private val _quizListState = quizRepository.quizListState
    val quizListState = _quizListState.asStateFlow()

    init {
        if (quiz.value?.id != id) {
            getQuizById(id)
        }
    }

    // Todo przechwytywanie bledow
    private fun getQuizById(id: UUID) {
        viewModelScope.launch {
            _quiz.value = null
            quizRepository.getQuizById(id).whenSuccess { _quiz.value = it.data }
        }
    }

    fun changeFavoriteStatus(id: UUID) {
        viewModelScope.launch {
            quizRepository.changeFavoriteStatus(id)
                .whenError {
                    _uiState.value = UiState.Error(it.error.toResId())
                }
        }
    }
    
    fun addQuizToMyHistory(id: UUID){
        viewModelScope.launch { 
            quizRepository.markQuizAsPlayed(id)
        }
    }
}