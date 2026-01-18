package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

import com.github.projektmagma.magmaquiz.shared.data.domain.Answer
import java.util.UUID

data class AnswerModel(
    val id: UUID? = null,
    val content: String = "",
    val isCorrect: Boolean = false
)

fun Answer.toAnswerModel(): AnswerModel {
    return AnswerModel(
        id = this.id,
        content = this.answerContent,
        isCorrect = this.isCorrect
    )
}