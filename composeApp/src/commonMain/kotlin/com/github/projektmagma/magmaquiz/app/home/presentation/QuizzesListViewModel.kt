package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.home.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class QuizzesListViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {
    var quizName by mutableStateOf("")

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes = _quizzes.asStateFlow()

    val isOnFavorites = mutableStateOf(false)

    var searchLock = false

    init {
        getQuizByName(false)
    }

    // Todo przechwytywanie bledow
    fun getQuizByName(withDelay: Boolean = false) {
        viewModelScope.launch {
            if (searchLock && withDelay) return@launch
            _uiState.value = UiState.Loading
            searchLock = true
            withSearchDelay(withDelay) {
                quizRepository.getQuizByName(quizName).whenSuccess {
                    _quizzes.value = it.data
                    _uiState.value = UiState.Success
                }.whenError {
                    _uiState.value = UiState.Error(it.error.toResId())
                }
                searchLock = false
            }
        }
    }

    fun changeFavoriteStatus(id: UUID) {
        viewModelScope.launch {
            quizRepository.changeFavoriteStatus(id)
            _quizzes.value.first { it.id == id }.apply {
                if (likedByYou) {
                    likedByYou = false
                    likesCount--
                } else {
                    likedByYou = true
                    likesCount++
                }
            }
        }
    }

    fun getMyFavorites() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            quizRepository.getMyFavorites()
                .whenSuccess {
                    _quizzes.value = it.data
                    _uiState.value = UiState.Success
                }.whenError {
                    _uiState.value = UiState.Error(it.error.toResId())
                }
        }
    }
}