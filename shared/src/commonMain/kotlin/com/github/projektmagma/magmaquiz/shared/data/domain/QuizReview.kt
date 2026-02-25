package com.github.projektmagma.magmaquiz.shared.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class QuizReview(
    val author: ForeignUser,
    val rating: Int,
    val comment: String
)
