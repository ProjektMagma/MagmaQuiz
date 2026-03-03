package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.users.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel(
    private val quizRepository: QuizRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    
    private val _recentQuizzes = quizRepository.recentQuizzes
    val recentQuizzes = _recentQuizzes.asStateFlow()
    private val _mostLikedQuizzes = quizRepository.mostLikedQuizzes
    val mostLikedQuizzes = _mostLikedQuizzes.asStateFlow()
    private val _friendsQuizzes = quizRepository.friendsQuizzes
    val friendsQuizzes = _friendsQuizzes.asStateFlow()

    private val _incomingFriends = MutableStateFlow<List<ForeignUser>>(emptyList())
    val incomingFriends = _incomingFriends.asStateFlow()

    init {
        downloadAllData()
    }

    // TODO: to na komendy można przepisać, jeśli nam się chce... zzz...
    fun downloadAllData() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            quizRepository.getRecentlyAddedQuizzes(count = 10).whenSuccess {
                _recentQuizzes.value = it.data
            }
            quizRepository.getMostLikedQuizzes(count = 10).whenSuccess {
                _mostLikedQuizzes.value = it.data
            }
            quizRepository.getFriendsQuizzes(count = 10).whenSuccess {
                _friendsQuizzes.value = it.data
            }
            usersRepository.getIncomingInvitations().whenSuccess {
                _incomingFriends.value = it.data
            }
            _uiState.value = UiState.Success
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
}