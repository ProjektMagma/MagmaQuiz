package com.github.projektmagma.magmaquiz.app.home.presentation.model.game

import java.util.UUID

sealed interface GameCommand {
    data class GetQuizById(val id: UUID): GameCommand
    data class AnswerClicked(val isCorrect: Boolean? = null, val content: String = ""): GameCommand
    data object StartGame: GameCommand
}