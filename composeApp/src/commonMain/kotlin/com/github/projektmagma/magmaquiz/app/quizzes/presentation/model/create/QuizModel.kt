package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create

import com.github.projektmagma.magmaquiz.shared.data.domain.QuizVisibility
import java.util.UUID

data class QuizModel(
    val id: UUID? = null,
    val name: String = "",
    val description: String = "",
    val image: ByteArray? = null,
    val visibility: QuizVisibility = QuizVisibility.Public,
    val tagList: List<String> = emptyList(),
    val questionList: List<QuestionModel> = emptyList()
)