package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

import java.util.UUID

data class QuizModel(
    val id: UUID? = null,
    val name: String = "",
    val description: String = "",
    val image: ByteArray? = null,
    val isPublic: Boolean = false,
    val questionList: List<QuestionModel> = emptyList()
)