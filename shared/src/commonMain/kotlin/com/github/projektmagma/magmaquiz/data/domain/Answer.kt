package com.github.projektmagma.magmaquiz.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class Answer(
    val id: Int? = null,
    val answerContent: String,
    val isCorrect: Boolean
)
