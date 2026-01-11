package com.github.projektmagma.magmaquiz.app.home.presentation.model

import org.jetbrains.compose.resources.StringResource

sealed interface UiEvent {
    data class ShowSnackbar(val id: StringResource?): UiEvent
    data object NavigateBack: UiEvent
}