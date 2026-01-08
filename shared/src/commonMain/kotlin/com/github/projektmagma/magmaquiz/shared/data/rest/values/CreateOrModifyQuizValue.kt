package com.github.projektmagma.magmaquiz.shared.data.rest.values

import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateOrModifyQuizValue(
    @Serializable(UUIDSerializer::class)
    val id: UUID? = null,
    val quizName: String,
    val quizDescription: String,
    val quizImage: ByteArray? = null,
    val isPublic: Boolean,
    val questionList: List<Question>
)