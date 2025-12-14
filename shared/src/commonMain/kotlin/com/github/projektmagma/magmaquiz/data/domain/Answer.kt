package com.github.projektmagma.magmaquiz.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class Answer(
    val answerContent: String,
    val isCorrect: Boolean
)
