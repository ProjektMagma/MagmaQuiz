package com.github.projektmagma.magmaquiz.app.quizzes.domain.validators

import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.QuizModel
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.description_not_provided
import magmaquiz.composeapp.generated.resources.name_empty
import magmaquiz.composeapp.generated.resources.no_questions
import org.jetbrains.compose.resources.StringResource

fun validateQuiz(quizModel: QuizModel): QuizError? {
    if (quizModel.name.isEmpty()) { return QuizError.NAME_EMPTY }
    if (quizModel.description.isEmpty()) { return QuizError.DESCRIPTION_EMPTY }
    if (quizModel.questionList.count() == 0) { return QuizError.NO_QUESTIONS }
    return null
}

enum class QuizError: Error {
    NAME_EMPTY,
    DESCRIPTION_EMPTY,
    NO_QUESTIONS
}

fun QuizError.toResId(): StringResource{
    return when (this) {
        QuizError.NAME_EMPTY -> Res.string.name_empty
        QuizError.DESCRIPTION_EMPTY -> Res.string.description_not_provided
        QuizError.NO_QUESTIONS -> Res.string.no_questions
    }
}