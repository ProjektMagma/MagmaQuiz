package com.github.projektmagma.magmaquiz.app.home.presentation.model

import java.util.UUID

data class AnswerState(
    val id: UUID = UUID.randomUUID(),
    val content: String = "",
    val isCorrect: Boolean = false
)