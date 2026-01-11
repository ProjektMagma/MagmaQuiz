package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

import com.github.projektmagma.magmaquiz.app.home.domain.validators.QuestionError
import com.github.projektmagma.magmaquiz.app.home.domain.validators.QuizError

data class CreateQuizState(
    val quizModel: QuizModel = QuizModel(),
    val questionModel: QuestionModel = QuestionModel(),
    val quizError: QuizError? = null,
    val questionError: QuestionError? = null
)