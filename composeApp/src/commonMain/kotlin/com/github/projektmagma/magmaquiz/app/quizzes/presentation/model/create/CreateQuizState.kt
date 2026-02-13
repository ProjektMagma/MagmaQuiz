package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create

import com.github.projektmagma.magmaquiz.app.quizzes.domain.validators.QuestionError
import com.github.projektmagma.magmaquiz.app.quizzes.domain.validators.QuizError

data class CreateQuizState(
    val quizModel: QuizModel = QuizModel(),
    val questionModel: QuestionModel = QuestionModel(),
    val quizError: QuizError? = null,
    val questionError: QuestionError? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false
)