package com.github.projektmagma.magmaquiz.app.home.presentation.model.game

data class AnswerState(
    val content: String,
    val isCorrect: Boolean,
    val isSelected: Boolean = false
)
