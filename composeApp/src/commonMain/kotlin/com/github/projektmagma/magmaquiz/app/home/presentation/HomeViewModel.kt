package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.home.HomeScreenCommand
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.users.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(
    private val quizRepository: QuizRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _recentQuizzesUiState = MutableStateFlow<UiState>(UiState.Loading)
    val recentQuizzesUiState = _recentQuizzesUiState.asStateFlow()

    private val _mostLikedQuizzesUiState = MutableStateFlow<UiState>(UiState.Loading)
    val mostLikedQuizzesUiState = _mostLikedQuizzesUiState.asStateFlow()

    private val _incomingFriendsUiState = MutableStateFlow<UiState>(UiState.Loading)
    val incomingFriendsUiState = _incomingFriendsUiState.asStateFlow()

    private val _friendsQuizzesUiState = MutableStateFlow<UiState>(UiState.Loading)
    val friendsQuizzesUiState = _friendsQuizzesUiState.asStateFlow()


    private val _changedFavoriteUiState = MutableStateFlow<UiState>(UiState.Success)
    val changedFavoriteUiState = _changedFavoriteUiState.asStateFlow()

    private val _recentQuizzes = quizRepository.recentQuizzes
    val recentQuizzes = _recentQuizzes.asStateFlow()
    private val _mostLikedQuizzes = quizRepository.mostLikedQuizzes
    val mostLikedQuizzes = _mostLikedQuizzes.asStateFlow()
    private val _friendsQuizzes = quizRepository.friendsQuizzes
    val friendsQuizzes = _friendsQuizzes.asStateFlow()

    private val _incomingFriends = MutableStateFlow<List<ForeignUser>>(emptyList())
    val incomingFriends = _incomingFriends.asStateFlow()

    init {
        viewModelScope.launch {
            awaitAll(
                async { friendsQuizzes() },
                async { incomingFriends() },
                async { mostLikedQuizzes() },
                async { recentlyAddedQuizzes() }
            )

        }
    }


    fun onCommand(homeScreenCommand: HomeScreenCommand) {
        viewModelScope.launch {
            when (homeScreenCommand) {
                HomeScreenCommand.FriendsQuizzes -> friendsQuizzes()
                HomeScreenCommand.IncomingFriends -> incomingFriends()
                HomeScreenCommand.MostLikedQuizzes -> mostLikedQuizzes()
                HomeScreenCommand.RecentQuizzes -> recentlyAddedQuizzes()
                is HomeScreenCommand.ChangeFavorite -> changeFavoriteStatus(homeScreenCommand.id)
            }
        }
    }

    private suspend fun recentlyAddedQuizzes() {
        quizRepository.getRecentlyAddedQuizzes(count = 10).whenSuccess {
            _recentQuizzes.value = it.data
            _recentQuizzesUiState.value = UiState.Success
        }

    }

    private suspend fun mostLikedQuizzes() {
        quizRepository.getMostLikedQuizzes(count = 10).whenSuccess {
            _mostLikedQuizzes.value = it.data
            _mostLikedQuizzesUiState.value = UiState.Success
        }
    }

    private suspend fun incomingFriends() {
        usersRepository.getIncomingInvitations().whenSuccess {
            _incomingFriends.value = it.data
            _incomingFriendsUiState.value = UiState.Success
        }
    }


    private suspend fun friendsQuizzes() {
        quizRepository.getFriendsQuizzes(count = 10).whenSuccess {
            _friendsQuizzes.value = it.data
            _friendsQuizzesUiState.value = UiState.Success
        }
    }


    private fun changeFavoriteStatus(id: UUID) {
        viewModelScope.launch {
            quizRepository.changeFavoriteStatus(id)
                .whenError {
                    _changedFavoriteUiState.value = UiState.Error(it.error.toResId())
                }
        }
    }
}