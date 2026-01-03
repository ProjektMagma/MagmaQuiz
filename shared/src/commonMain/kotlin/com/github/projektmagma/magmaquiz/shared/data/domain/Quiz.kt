package com.github.projektmagma.magmaquiz.shared.data.domain

import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Quiz(
    @Serializable(UUIDSerializer::class)
    val id: UUID? = null,
    val quizName: String,
    val quizDescription: String? = null,
    val quizImage: ByteArray? = null,
    val isPublic: Boolean,
    val quizCreatorName: String? = null,
    val likesCount: Int,
    var likedByYou: Boolean? = null,
    val createdAt: Long? = null,
    val modifiedAt: Long? = null,
    val questionList: List<Question>? = null
)
