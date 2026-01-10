package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

import io.github.vinceglb.filekit.PlatformFile

data class QuestionState(
    val number: Int = 1,
    val content: String = "",
    val image: PlatformFile? = null,
    val answerList: List<AnswerState> = emptyList()
)