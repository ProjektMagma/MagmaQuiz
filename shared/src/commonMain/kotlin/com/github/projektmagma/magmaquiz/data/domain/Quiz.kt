package com.github.projektmagma.magmaquiz.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val id: Int? = null,
    val quizName: String,
    val quizDescription: String? = null,
    val quizImage: ByteArray? = null,
    val isPublic: Boolean,
    val quizCreatorName: String? = null,
    val createdAt: Long? = null,
    val modifiedAt: Long? = null,
    val questionList: List<Question>? = null
)
