package com.github.projektmagma.magmaquiz.app.users.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.users.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class UserDetailsViewModel(
    private val quizRepository: QuizRepository,
    private val usersRepository: UsersRepository,
    private val authRepository: AuthRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _quizzes = quizRepository.userDetailsQuizList
    val quizzes = _quizzes.asStateFlow()
    
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()
    
    fun checkOwnership(id: UUID): Boolean{
        return id == authRepository.thisUser.value?.userId
    }
    
    fun loadData(id: UUID){
        getQuizzesByUserId(id)
        getUserData(id)
    }

    private fun getQuizzesByUserId(id: UUID) {
        viewModelScope.launch { 
            _uiState.value = UiState.Loading
            quizRepository.getQuizzesByUserId(id)
                .whenSuccess { 
                    _uiState.value = UiState.Success
                    _quizzes.value = it.data
                }
                .whenError { _uiState.value = UiState.Error(it.error.toResId()) }
        }
    }
    
    private fun getUserData(id: UUID) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
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
                .whenSuccess {
                    _uiState.value = UiState.Success
                }
                .whenError { 
                    _uiState.value = UiState.Error(it.error.toResId())
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