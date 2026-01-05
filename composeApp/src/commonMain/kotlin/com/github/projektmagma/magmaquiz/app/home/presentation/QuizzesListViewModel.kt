package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.home.data.QuizRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizzesListViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {
    var quizName by mutableStateOf("")

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes = _quizzes.asStateFlow()


    // Todo przechwytywanie bledow
    fun getQuizByName(withDelay: Boolean = false) {
        viewModelScope.launch {
            withSearchDelay(withDelay) {
                quizRepository.getQuizByName(quizName).whenSuccess { _quizzes.value = it.data }
            }
        }
    }
}