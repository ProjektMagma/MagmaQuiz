package com.github.projektmagma.magmaquiz.shared.data.domain

import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Quiz(
    @Serializable(UUIDSerializer::class)
    val id: UUID? = null,
    val quizName: String,
    val quizDescription: String,
    val quizImage: ByteArray? = null,
    val isPublic: Boolean,
    val quizCreator: ForeignUser,
    val likesCount: Int,
    var likedByYou: Boolean = false,
    val createdAt: Long,
    val modifiedAt: Long,
    val questionList: List<Question>
)
