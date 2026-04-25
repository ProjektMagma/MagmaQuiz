package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.util.Paginator
import com.github.projektmagma.magmaquiz.app.game.data.repository.GameRepository
import com.github.projektmagma.magmaquiz.app.home.presentation.model.main.HomeScreenCommand
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.users.data.repository.UsersRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel(
    private val quizRepository: QuizRepository,
    private val usersRepository: UsersRepository,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _recentQuizzesUiState = MutableStateFlow<UiState>(UiState.Loading)
    val recentQuizzesUiState = _recentQuizzesUiState.asStateFlow()

    private val _mostLikedQuizzesUiState = MutableStateFlow<UiState>(UiState.Loading)
    val mostLikedQuizzesUiState = _mostLikedQuizzesUiState.asStateFlow()

    private val _incomingFriendsUiState = MutableStateFlow<UiState>(UiState.Loading)
    val incomingFriendsUiState = _incomingFriendsUiState.asStateFlow()

    private val _friendsQuizzesUiState = MutableStateFlow<UiState>(UiState.Loading)
    val friendsQuizzesUiState = _friendsQuizzesUiState.asStateFlow()
    
    private val _state = quizRepository.homeState
    val state = _state.asStateFlow()
    
    val paginatorLikedQuizzes = Paginator(
        initialKey = 0,
        onLoadUpdated = { isLoading -> 
            _state.update { it.copy(isLoadingMoreLiked = isLoading) }
        },
        onRequest = { currentPage -> quizRepository.getMostLikedQuizzes(offset = currentPage) },
        getNextKey = { currentKey, _ -> currentKey + 1 },
        onError = { networkError -> _mostLikedQuizzesUiState.value = UiState.Error(networkError.toResId()) },
        onSuccess = { result, _ ->  
            _state.update { it.copy(mostLikedQuizzes = it.mostLikedQuizzes + result) }
            _mostLikedQuizzesUiState.value = UiState.Success
        },
        endReached = { _, items -> items.isEmpty() }
    )
    
    val paginatorRecentlyQuizzes = Paginator(
        initialKey = 0,
        onLoadUpdated = { isLoading ->
            _state.update { it.copy(isLoadingMoreRecent = isLoading) }
        },
        onRequest = { currentPage -> quizRepository.getRecentlyAddedQuizzes(offset = currentPage) },
        getNextKey = { currentKey, _ -> currentKey + 1 },
        onError = { networkError -> _recentQuizzesUiState.value = UiState.Error(networkError.toResId()) },
        onSuccess = { result, _ ->  
            _state.update { it.copy(recentQuizzes = it.recentQuizzes + result) }
            _recentQuizzesUiState.value = UiState.Success
        },
        endReached = { _, items -> items.isEmpty() }
    )
    
    val paginatorIncoming = Paginator(
        initialKey = 0,
        onLoadUpdated = { isLoading -> 
            _state.update { it.copy(isLoadingMoreFriends = isLoading) }
        },
        onRequest = { currentPage -> usersRepository.getIncomingInvitations(offset = currentPage) },
        getNextKey = { currentKey, _ -> currentKey + 1 },
        onError = { networkError -> _incomingFriendsUiState.value = UiState.Error(networkError.toResId()) },
        onSuccess = { result, _ ->  
            _state.update { it.copy(incomingFriends = it.incomingFriends + result) }
            _incomingFriendsUiState.value = UiState.Success
        },
        endReached = { _, items -> items.isEmpty() }
    )
    
    val paginatorFriendQuizzes = Paginator(
        initialKey = 0,
        onLoadUpdated = { isLoading -> 
            _state.update { it.copy(isLoadingMoreFriendsQuizzes = isLoading) }
        },
        onRequest = { currentPage -> quizRepository.getFriendsQuizzes(offset = currentPage) },
        getNextKey = { currentKey, _ -> currentKey + 1 },
        onError = { networkError -> _friendsQuizzesUiState.value = UiState.Error(networkError.toResId()) },
        onSuccess = { result, _ ->  
            _state.update { it.copy(friendsQuizzes = it.friendsQuizzes + result) }
            _friendsQuizzesUiState.value = UiState.Success
        },
        endReached = { _, items -> items.isEmpty() }
    )
    
    private fun loadNextLikedQuizzes(){
        viewModelScope.launch { 
            paginatorLikedQuizzes.loadNextItems()
        }
    }
    
    private fun loadNextRecentlyQuizzes(){
        viewModelScope.launch {
            paginatorRecentlyQuizzes.loadNextItems()
        }
    }
    
    private fun loadNextIncoming(){
        viewModelScope.launch {
            paginatorIncoming.loadNextItems()
        }
    }
    
    private fun loadNextFriendsQuizzes() {
        viewModelScope.launch {
            paginatorFriendQuizzes.loadNextItems()
        }
    }

    init {
        viewModelScope.launch {
            awaitAll(
                async { paginatorFriendQuizzes.loadNextItems() },
                async { paginatorIncoming.loadNextItems() },
                async { paginatorLikedQuizzes.loadNextItems() },
                async { paginatorRecentlyQuizzes.loadNextItems() },
            )
        }
    }

    fun onCommand(homeScreenCommand: HomeScreenCommand) {
        viewModelScope.launch {
            when (homeScreenCommand) {
                HomeScreenCommand.FriendsQuizzes -> loadNextFriendsQuizzes()
                HomeScreenCommand.IncomingFriends -> loadNextIncoming()
                HomeScreenCommand.MostLikedQuizzes -> loadNextLikedQuizzes()
                HomeScreenCommand.RecentQuizzes -> loadNextRecentlyQuizzes()
                is HomeScreenCommand.ChangeFavorite -> changeFavoriteStatus(homeScreenCommand.id)
            }
        }
    }
    
    private fun changeFavoriteStatus(id: UUID) {
        viewModelScope.launch {
            quizRepository.changeFavoriteStatus(id)
        }
    }
}