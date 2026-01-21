package com.github.projektmagma.magmaquiz.app.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.AuthState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RootViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state = _state.asStateFlow()
    
    init {
        checkUserStatus()
    }
    
    private fun checkUserStatus() {
        viewModelScope.launch {
            _state.value = when (authRepository.whoAmI()) {
                is Resource.Error -> AuthState.Unauthenticated
                is Resource.Success -> AuthState.Authenticated
            }
        }
    }
}