package com.github.projektmagma.magmaquiz.app.users.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.util.Paginator
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.users.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.app.users.presentation.model.details.UserDetailsCommand
import com.github.projektmagma.magmaquiz.app.users.presentation.model.details.UserDetailsState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class UserDetailsViewModel(
    private val id: UUID,
    private val quizRepository: QuizRepository,
    private val usersRepository: UsersRepository,
    private val authRepository: AuthRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private val _state = MutableStateFlow(UserDetailsState())
    private val _quizzes = quizRepository.userDetailsStateQuizzes

    val state = combine(
        _state,
        _quizzes
    ) { state, quizzes ->
        UserDetailsState(
            selectedTabIndex = state.selectedTabIndex,
            quizzes = quizzes,
            user = state.user,
            isLoadingMore = state.isLoadingMore
        )
    }
    
    val paginator = Paginator(
        initialKey = 0,
        onLoadUpdated = { isLoading ->  _state.update { it.copy(isLoadingMore = isLoading) } },
        onRequest = { currentPage ->
            if (_state.value.selectedTabIndex == 0) {
                quizRepository.getQuizzesByUserId(id, offset = currentPage)
            } else {
                quizRepository.getMyGameHistory(offset = currentPage)
            } 
        },
        getNextKey = { key, _ -> key + 1 },
        onError = { networkError -> _uiState.value = UiState.Error(networkError.toResId()) },
        onSuccess = { item, _ -> _quizzes.value += item },
        endReached = { _, item -> item.isEmpty() }
    )

    fun onCommand(command: UserDetailsCommand) {
        when (command) {
            is UserDetailsCommand.SelectedTabIndexChanged ->  _state.update { it.copy(selectedTabIndex = command.newIndex) }
            is UserDetailsCommand.LoadNextItems -> loadNextItems()
            is UserDetailsCommand.LoadData -> {
                loadNextItems()
                getUserData()
            }
            is UserDetailsCommand.LoadQuizzesOrHistory -> {
                paginator.reset()
                _state.update { it.copy(quizzes = emptyList()) }
                loadNextItems()
            }
            is UserDetailsCommand.GetUserData -> getUserData()
            is UserDetailsCommand.ChangeFavoriteStatus -> changeFavoriteStatus(command.id)
            is UserDetailsCommand.DeleteQuiz -> deleteQuiz(command.id)
        }
    }
    
    init {
        _quizzes.value = emptyList()
        loadNextItems()
        getUserData()
    }
    
    private fun loadNextItems(){
        viewModelScope.launch { 
            paginator.loadNextItems()
        }
    }
    
    fun checkOwnership(id: UUID): Boolean{
        return id == authRepository.thisUser.value?.userId
    }
    
    private fun getUserData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            if (checkOwnership(id)) {
                _state.update { it.copy(user = authRepository.thisUser.value) }
                _uiState.value = UiState.Success
            } else {
                usersRepository.getUserDataById(id)
                    .whenSuccess { result ->
                        _uiState.value = UiState.Success
                        _state.update { it.copy(user = result.data) }
                    }
                    .whenError { _uiState.value = UiState.Error(it.error.toResId()) }
            }
        }
    }

    private fun changeFavoriteStatus(id: UUID) {
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
    
    private fun deleteQuiz(id: UUID){
        viewModelScope.launch { 
            quizRepository.deleteQuiz(id)
                .whenSuccess {
                    _uiState.value = UiState.Success
                    quizRepository.deleteQuizInList(id)
                }
                .whenError { _uiState.value = UiState.Error(it.error.toResId()) }
        }
    }
}