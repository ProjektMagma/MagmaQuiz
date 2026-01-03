package com.github.projektmagma.magmaquiz.app.core.presentation.model.server

sealed interface ServerEvent {
    data object Success: ServerEvent
    data object Failure: ServerEvent
}