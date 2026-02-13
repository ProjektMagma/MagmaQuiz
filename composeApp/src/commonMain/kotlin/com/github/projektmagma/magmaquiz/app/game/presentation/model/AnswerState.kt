package com.github.projektmagma.magmaquiz.app.game.presentation.model

data class AnswerState(
    val content: String,
    val isCorrect: Boolean,
    val isSelected: Boolean = false
)
