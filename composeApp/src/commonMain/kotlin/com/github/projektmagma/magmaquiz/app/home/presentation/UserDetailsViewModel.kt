package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.home.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.app.home.presentation.model.UiEvent
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class UserDetailsViewModel(
    private val quizRepository: QuizRepository,
    private val usersRepository: UsersRepository,
    private val authRepository: AuthRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes = _quizzes.asStateFlow()
    
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()
    
    fun checkOwnership(id: UUID): Boolean{
        return id == authRepository.thisUser.value?.userId
    }

    fun getQuizzesByUserId(id: UUID) {
        viewModelScope.launch { 
            quizRepository.getQuizzesByUserId(id)
                .whenSuccess { 
                    _uiState.value = UiState.Success
                    _quizzes.value = it.data
                }
                .whenError { _uiState.value = UiState.Error(it.error.toResId()) }
        }
    }
    
    fun getUserData(id: UUID) {
        viewModelScope.launch { 
            if (checkOwnership(id)) {
                _user.value = authRepository.thisUser.value
            } else {
                usersRepository.getUserDataById(id)
                    .whenSuccess {
                        _user.value = it.data
                    }
                    .whenError { _uiState.value = UiState.Error(it.error.toResId()) }
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
    
    fun deleteQuiz(id: UUID){
        viewModelScope.launch { 
            quizRepository.deleteQuiz(id)
                .whenSuccess {
                     _quizzes.update { quizzes -> 
                        quizzes.filter { quiz ->
                            quiz.id != id
                        }
                    }
                    _uiState.value = UiState.Success 
                }
                .whenError { _uiState.value = UiState.Error(it.error.toResId()) }
        }
    }
}