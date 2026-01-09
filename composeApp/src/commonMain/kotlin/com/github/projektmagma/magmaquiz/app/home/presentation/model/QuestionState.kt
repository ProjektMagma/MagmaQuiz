package com.github.projektmagma.magmaquiz.app.home.presentation.model

data class QuestionState(
    val number: Int = 1,
    val content: String = "",
    val image: ByteArray? = null,
    val answerList: List<AnswerState> = emptyList()
)