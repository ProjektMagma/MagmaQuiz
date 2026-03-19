package com.github.projektmagma.magmaquiz.app.quizzes.domain.mappers

import com.github.projektmagma.magmaquiz.app.quizzes.domain.model.QuizReviewModel
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizReview
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User

fun QuizReview.toModel(author: User? = null): QuizReviewModel{
    return QuizReviewModel(
        author = author ?: this.author,
        rating = this.rating,
        comment = this.comment,
    )
}