package com.github.projektmagma.magmaquiz.app.quizzes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizFilters
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizListCommand
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
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
    
    var searchLock = false

    init {
        getQuizByName(false)
    }
    
    fun onCommand(command: QuizListCommand){
        when (command) {
            is QuizListCommand.NameChanged -> _quizListState.update { it.copy(quizName = command.name) }
            is QuizListCommand.FilterChanged -> _quizListState.update { it.copy(isLoaded = false, quizFilter = command.filter) }
            is QuizListCommand.ListChanged -> _quizListState.update { it.copy(quizzes = command.list) }
            QuizListCommand.LoadByFilter -> loadByFilter()
            is QuizListCommand.LoadByName -> getQuizByName(command.delay)
            is QuizListCommand.FavoriteStatusChanged -> changeFavoriteStatus(command.id)
        }
    }
    
    private fun loadByFilter() {
        when (_quizListState.value.quizFilter){
            QuizFilters.Favorites -> getMyFavorites()
            QuizFilters.Friends -> getFriendsQuizzes()
            QuizFilters.MostLiked -> getMostLikedQuizzes()
            QuizFilters.None -> getQuizByName(false)
            QuizFilters.RecentlyAdded -> getRecentlyAddedQuizzes()
        }
    }

    private fun getQuizByName(withDelay: Boolean = false) {
        viewModelScope.launch {
            if (searchLock && withDelay) return@launch
            _uiState.value = UiState.Loading
            searchLock = true
            withSearchDelay(withDelay) {
                quizRepository.getQuizByName(_quizListState.value.quizName).whenSuccess {
                    _quizListState.update { state -> state.copy(quizzes = it.data, isLoaded = true) }
                    _uiState.value = UiState.Success
                }.whenError {
                    _uiState.value = UiState.Error(it.error.toResId())
                }
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

    private fun getMyFavorites() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            quizRepository.getMyFavorites()
                .whenSuccess {
                    _quizListState.update { state -> state.copy(quizzes = it.data, isLoaded = true) }
                    _uiState.value = UiState.Success
                }.whenError {
                    _uiState.value = UiState.Error(it.error.toResId())
                }
        }
    }

    private fun getMostLikedQuizzes() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            quizRepository.getMostLikedQuizzes()
                .whenSuccess {
                    _quizListState.update { state -> state.copy(quizzes = it.data, isLoaded = true) }
                    _uiState.value = UiState.Success
                }.whenError {
                    _uiState.value = UiState.Error(it.error.toResId())
                }
        }
    }

    private fun getFriendsQuizzes() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            quizRepository.getFriendsQuizzes()
                .whenSuccess {
                    _quizListState.update { state -> state.copy(quizzes = it.data, isLoaded = true) }
                    _uiState.value = UiState.Success
                }.whenError {
                    _uiState.value = UiState.Error(it.error.toResId())
                }
        }
    }

    private fun getRecentlyAddedQuizzes() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            quizRepository.getRecentlyAddedQuizzes()
                .whenSuccess {
                    _quizListState.update { state -> state.copy(quizzes = it.data, isLoaded = true) }
                    _uiState.value = UiState.Success
                }.whenError {
                    _uiState.value = UiState.Error(it.error.toResId())
                }
        }
    }
}