package com.github.projektmagma.magmaquiz.app.core.presentation.model.events

sealed interface LocalEvent {
    data object Success : LocalEvent
    data object Failure : LocalEvent
}