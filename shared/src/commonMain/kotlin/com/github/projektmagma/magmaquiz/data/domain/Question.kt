package com.github.projektmagma.magmaquiz.data.domain

import kotlinx.serialization.Serializable

@Serializable

data class Question(
    val id: Int? = null,
    val questionNumber: Int,
    val questionContent: String,
    val questionImage: ByteArray? = null,
    val answerList: List<Answer>? = null
)
