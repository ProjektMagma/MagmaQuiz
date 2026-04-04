package com.github.projektmagma.magmaquiz.app.quizzes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.util.Paginator
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizFilters
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.list.QuizListCommand
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.list.QuizListState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class QuizzesListViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _quizzesListQuizzes = quizRepository.quizListQuizzes
    private val _quizName = MutableStateFlow("")
    private val _quizFilter = MutableStateFlow<QuizFilters>(QuizFilters.None)
    private val _isLoadingMore = MutableStateFlow(false)
    
    val quizListState = combine(
        _quizName,
        _quizFilter,
        _quizzesListQuizzes,
        _isLoadingMore
    ) { name, filter, quizzesList, isLoadingMore ->
        QuizListState(
            quizName = name,
            quizzes = quizzesList,
            quizFilter = filter,
            isLoadingMore = isLoadingMore
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        QuizListState()
    )
    
    val paginator = Paginator(
        initialKey = 0,
        onLoadUpdated = { isLoading ->
            _isLoadingMore.value = isLoading
        },
        onRequest = { currentPage ->
            val name = _quizName.value
            when (_quizFilter.value) {
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
            _quizzesListQuizzes.value = (_quizzesListQuizzes.value + items).distinctBy { it.id }
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
            is QuizListCommand.NameChanged -> _quizName.value = command.name
            is QuizListCommand.FilterChanged -> {
                _uiState.value = UiState.Loading
                paginator.reset()
                _quizFilter.value = command.filter
                _quizzesListQuizzes.value = emptyList()
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
                _quizzesListQuizzes.value = emptyList()
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