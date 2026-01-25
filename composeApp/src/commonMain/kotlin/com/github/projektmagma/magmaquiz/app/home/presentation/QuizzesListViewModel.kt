package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.home.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizFilters
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizListCommand
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizListState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class QuizzesListViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    var quizListState by mutableStateOf(QuizListState())

    var searchLock = false

    init {
        getQuizByName(false)
    }
    
    fun onCommand(command: QuizListCommand){
        when (command) {
            is QuizListCommand.NameChanged -> quizListState = quizListState.copy(quizName = command.name)
            is QuizListCommand.FilterChanged -> {
                quizListState = quizListState.copy(isLoaded = false)
                quizListState = quizListState.copy(quizFilter = command.filter)
            }
            is QuizListCommand.ListChanged -> quizListState = quizListState.copy(quizzes = command.list)
            QuizListCommand.LoadByFilter -> loadByFilter()
            is QuizListCommand.LoadByName -> getQuizByName(command.delay)
            is QuizListCommand.FavoriteStatusChanged -> changeFavoriteStatus(command.id)
        }
    }
    
    private fun loadByFilter() {
        when (quizListState.quizFilter){
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
                quizRepository.getQuizByName(quizListState.quizName).whenSuccess {
                    quizListState = quizListState.copy(quizzes = it.data, isLoaded = true)
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
            quizListState.quizzes.first { it.id == id }.apply {
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

    private fun getMyFavorites() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            quizRepository.getMyFavorites()
                .whenSuccess {
                    quizListState = quizListState.copy(quizzes = it.data, isLoaded = true)
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
                    quizListState = quizListState.copy(quizzes = it.data, isLoaded = true)
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
                    quizListState = quizListState.copy(quizzes = it.data, isLoaded = true)
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
                    quizListState = quizListState.copy(quizzes = it.data, isLoaded = true)
                    _uiState.value = UiState.Success
                }.whenError {
                    _uiState.value = UiState.Error(it.error.toResId())
                }
        }
    }
}