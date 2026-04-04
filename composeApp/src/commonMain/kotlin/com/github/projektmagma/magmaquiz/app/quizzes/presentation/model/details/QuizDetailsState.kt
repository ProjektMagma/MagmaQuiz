package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.details

import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz

data class QuizDetailsState(
    val quiz: Quiz? = null,
    val quizzes: List<Quiz> = emptyList()
)