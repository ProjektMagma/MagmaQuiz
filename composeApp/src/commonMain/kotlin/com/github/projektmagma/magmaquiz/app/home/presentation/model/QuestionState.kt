package com.github.projektmagma.magmaquiz.app.home.presentation.model

import java.util.UUID

data class QuestionState(
    val id: UUID = UUID.randomUUID(),
    val number: Int = 1,
    val content: String = "",
    val image: ByteArray? = null,
    val answerList: List<AnswerState> = emptyList()
)