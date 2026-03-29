package com.github.projektmagma.magmaquiz.app.quizzes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.util.Paginator
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizFilters
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizListCommand
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class QuizzesListViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _quizListState = quizRepository.quizListState
    val quizListState = _quizListState.asStateFlow()
    
    val paginator = Paginator(
        initialKey = 0,
        onLoadUpdated = { isLoading ->
            _quizListState.update { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { currentPage ->
            val name = _quizListState.value.quizName
            when (_quizListState.value.quizFilter) {
                QuizFilters.Favorites -> quizRepository.getMyFavorites(name, offset = currentPage)
                QuizFilters.Friends -> quizRepository.getFriendsQuizzes(name, offset = currentPage)
                QuizFilters.MostLiked -> quizRepository.getMostLikedQuizzes(name, offset = currentPage)
                QuizFilters.None -> quizRepository.getQuiz(name, offset = currentPage)
                QuizFilters.RecentlyAdded -> quizRepository.getRecentlyAddedQuizzes(name, offset = currentPage)
            }
        },
        getNextKey = { currentPage, _ ->
            currentPage + 1
        },
        onError = { networkError ->
            _uiState.value = UiState.Error(networkError.toResId())
        },
        onSuccess = { items, _ ->
            _quizListState.update { state ->
                state.copy(quizzes = state.quizzes + items)
            }
            _uiState.value = UiState.Success
        },
        endReached = { _, items ->
            items.isEmpty()
        }
    )
    
    init {
        loadNextItems()
    }

    var searchLock = false
    
    fun onCommand(command: QuizListCommand){
        when (command) {
            is QuizListCommand.NameChanged -> _quizListState.update { it.copy(quizName = command.name) }
            is QuizListCommand.FilterChanged -> {
                _uiState.value = UiState.Loading
                paginator.reset()
                _quizListState.update {
                    it.copy(
                        quizFilter = command.filter,
                        quizzes = emptyList()
                    )
                }
                getQuizByName(false)
            }
            is QuizListCommand.LoadByName -> getQuizByName(command.delay)
            QuizListCommand.LoadMore -> loadNextItems()
            is QuizListCommand.FavoriteStatusChanged -> changeFavoriteStatus(command.id)
        }
    }

    private fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun getQuizByName(withDelay: Boolean = false) {
        viewModelScope.launch {
            if (searchLock && withDelay) return@launch
            searchLock = true
            _uiState.value = UiState.Loading
            withSearchDelay(withDelay) {
                paginator.reset()
                _quizListState.update { it.copy(quizzes = emptyList()) }
                paginator.loadNextItems()
                searchLock = false
            }
        }
    }

    private fun changeFavoriteStatus(id: UUID) {
        viewModelScope.launch {
            quizRepository.changeFavoriteStatus(id)
                .whenError { 
                    _uiState.value = UiState.Error(it.error.toResId())
                }
        }
    }
}