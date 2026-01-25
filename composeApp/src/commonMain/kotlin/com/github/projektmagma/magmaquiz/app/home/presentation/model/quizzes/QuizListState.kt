package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz

data class QuizListState(
    val quizName: String = "",
    val quizzes: List<Quiz> = emptyList(),
    val quizFilter: QuizFilters = QuizFilters.None,
    val isLoaded: Boolean = false
)