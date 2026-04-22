package com.github.projektmagma.magmaquiz.app.game.presentation.model

sealed interface GameEvent {
    data object Success : GameEvent
    data class Closed(val message: String?) : GameEvent
}