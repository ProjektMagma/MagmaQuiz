package com.github.projektmagma.magmaquiz.app.home.presentation.model.game

sealed interface GameCommand {
    data class AnswerClicked(val isCorrect: Boolean? = null, val content: String = ""): GameCommand
    data object StartGame: GameCommand
}