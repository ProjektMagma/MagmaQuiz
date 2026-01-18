package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import java.util.UUID

data class QuestionModel(
    val id: UUID? = null,
    val number: Int = 1,
    val content: String = "",
    val image: ByteArray? = null,
    val answerList: List<AnswerModel> = emptyList()
)

fun Question.toQuestionModel(): QuestionModel {
    return QuestionModel(
        id = this.id,
        number = this.questionNumber,
        content = this.questionContent,
        image = this.questionImage,
        answerList = this.answerList.map { it.toAnswerModel() }
    )
}