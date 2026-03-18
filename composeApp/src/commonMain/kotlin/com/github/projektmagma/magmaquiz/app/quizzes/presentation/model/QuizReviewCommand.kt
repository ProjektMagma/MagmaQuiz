package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model

import java.util.UUID

sealed interface QuizReviewCommand {
    data class ContentChanged(val newContent: String): QuizReviewCommand
    data class RatingChanged(val newRating: Int): QuizReviewCommand
    data object GetReviews: QuizReviewCommand
    data object CreateReview: QuizReviewCommand
    data class DeleteReview(val id: UUID): QuizReviewCommand
}