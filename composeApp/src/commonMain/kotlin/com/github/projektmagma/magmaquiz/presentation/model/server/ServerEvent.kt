package com.github.projektmagma.magmaquiz.presentation.model.server

sealed interface ServerEvent {
    data object Success: ServerEvent
    data object Failure: ServerEvent
}