package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

import io.github.vinceglb.filekit.PlatformFile

data class QuestionModel(
    val number: Int = 1,
    val content: String = "",
    val image: PlatformFile? = null,
    val answerList: List<AnswerModel> = emptyList()
)