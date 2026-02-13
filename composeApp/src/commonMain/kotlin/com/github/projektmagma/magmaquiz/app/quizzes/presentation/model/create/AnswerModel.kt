package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create

import com.github.projektmagma.magmaquiz.shared.data.domain.Answer
import java.util.*

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