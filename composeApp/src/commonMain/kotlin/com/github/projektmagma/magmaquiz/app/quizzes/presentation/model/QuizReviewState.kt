package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model

import com.github.projektmagma.magmaquiz.shared.data.domain.QuizReview

data class QuizReviewState(
    val content: String = "",
    val rating: Int = 0,
    val isOwner: Boolean = false,
    val reviews: List<QuizReview> = emptyList(),
)
