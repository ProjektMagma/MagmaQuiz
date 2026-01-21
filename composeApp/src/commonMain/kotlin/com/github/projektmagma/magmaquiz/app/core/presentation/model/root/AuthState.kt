package com.github.projektmagma.magmaquiz.app.core.presentation.model.root

sealed interface AuthState {
    data object Loading : AuthState
    data object Authenticated : AuthState
    data object Unauthenticated : AuthState
}