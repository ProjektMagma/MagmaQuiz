package com.github.projektmagma.magmaquiz.app.game.presentation.model

sealed interface GameCommand {
    data class AnswerClicked(val isCorrect: Boolean? = null, val content: String = ""): GameCommand
    data object StartGame: GameCommand
    data object FinishGame: GameCommand
}