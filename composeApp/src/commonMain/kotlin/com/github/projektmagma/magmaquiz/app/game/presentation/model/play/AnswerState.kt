package com.github.projektmagma.magmaquiz.app.game.presentation.model.play

import java.util.UUID

data class AnswerState(
    val id: UUID,
    val content: String,
    val isCorrect: Boolean,
    val isSelected: Boolean = false
)
