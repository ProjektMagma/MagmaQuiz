package com.github.projektmagma.magmaquiz.app.core.presentation.model.root

import org.jetbrains.compose.resources.StringResource

sealed interface UiState {
    data object Loading : UiState
    data object Success : UiState
    data class Error(val errorMessage: StringResource) : UiState
}