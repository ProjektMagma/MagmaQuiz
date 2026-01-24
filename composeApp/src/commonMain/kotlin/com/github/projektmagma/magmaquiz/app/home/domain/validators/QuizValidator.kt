package com.github.projektmagma.magmaquiz.app.home.domain.validators

import com.github.projektmagma.magmaquiz.app.home.domain.validators.QuizError.DESCRIPTION_EMPTY
import com.github.projektmagma.magmaquiz.app.home.domain.validators.QuizError.NAME_EMPTY
import com.github.projektmagma.magmaquiz.app.home.domain.validators.QuizError.NO_QUESTIONS
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.create.QuizModel
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
        NAME_EMPTY -> Res.string.name_empty
        DESCRIPTION_EMPTY -> Res.string.description_not_provided
        NO_QUESTIONS -> Res.string.no_questions
    }
}