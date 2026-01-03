package com.github.projektmagma.magmaquiz.app.core.presentation.model.root

sealed interface UiState {
    data object Loading: UiState
    data object Authenticated: UiState
    data object Unauthenticated: UiState
}