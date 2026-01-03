package com.github.projektmagma.magmaquiz.app.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.UserRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RootViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state = _state.asStateFlow()
    
    init {
        checkUserStatus()
    }
    
    private fun checkUserStatus() {
        viewModelScope.launch { 
            _state.value = when (userRepository.whoAmI()){
                is Resource.Error -> UiState.Unauthenticated
                is Resource.Success -> UiState.Authenticated
            }
        }
    }
}