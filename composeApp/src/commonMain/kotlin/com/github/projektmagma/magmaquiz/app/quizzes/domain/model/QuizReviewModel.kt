package com.github.projektmagma.magmaquiz.app.quizzes.domain.model

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User

data class QuizReviewModel(
    val author: User?,
    val rating: Int,
    val comment: String
)