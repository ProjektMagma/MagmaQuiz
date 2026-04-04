package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.list

import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizFilters
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz

data class QuizListState(
    val quizName: String = "",
    val quizzes: List<Quiz> = emptyList(),
    val quizFilter: QuizFilters = QuizFilters.None,
    val isLoadingMore: Boolean = false
)