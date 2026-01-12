package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.home.data.QuizRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class QuizViewModel(
    private val quizRepository: QuizRepository
): ViewModel() {
    private val _quiz = MutableStateFlow<Quiz?>(null)
    val quiz = _quiz.asStateFlow()
    
    // Todo przechwytywanie bledow
    fun getQuizById(id: UUID){
        viewModelScope.launch {
            quizRepository.getQuizById(id).whenSuccess { _quiz.value = it.data }
        }
    }
    
    fun clearQuiz(){
        _quiz.value = null
    }
}