package com.github.projektmagma.magmaquiz.app.quizzes.domain.validators

import com.github.projektmagma.magmaquiz.app.quizzes.domain.validators.QuestionError.*
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.QuestionModel
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.answer_content_empty
import magmaquiz.composeapp.generated.resources.content_empty
import magmaquiz.composeapp.generated.resources.no_correct
import org.jetbrains.compose.resources.StringResource

fun validateQuestion(questionModel: QuestionModel): QuestionError?{
    if (questionModel.content.isEmpty()) {
        return CONTENT_EMPTY
    }
    if (questionModel.answerList.any { it.content.isEmpty() }) {
        return ANSWER_CONTENT_EMPTY
    }
    if (questionModel.answerList.count { it.isCorrect } == 0) {
        return NO_CORRECT
    }
    return null
} 

enum class QuestionError: Error {
    CONTENT_EMPTY,
    ANSWER_CONTENT_EMPTY,
    NO_CORRECT
}

fun QuestionError.toResId(): StringResource {
    return when (this) {
        CONTENT_EMPTY -> Res.string.content_empty
        ANSWER_CONTENT_EMPTY -> Res.string.answer_content_empty
        NO_CORRECT -> Res.string.no_correct
    }
}
