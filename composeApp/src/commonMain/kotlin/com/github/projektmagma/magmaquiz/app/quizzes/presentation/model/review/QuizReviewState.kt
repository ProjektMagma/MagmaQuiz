package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.review

import com.github.projektmagma.magmaquiz.app.quizzes.domain.model.QuizReviewModel

data class QuizReviewState(
    val content: String = "",
    val rating: Int = 0,
    val hasReviewed: Boolean,
    val reviews: List<QuizReviewModel> = emptyList(),
)
