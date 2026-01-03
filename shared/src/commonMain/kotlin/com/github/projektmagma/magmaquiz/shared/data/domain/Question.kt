package com.github.projektmagma.magmaquiz.shared.data.domain

import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable

data class Question(
    @Serializable(UUIDSerializer::class)
    val id: UUID? = null,
    val questionNumber: Int,
    val questionContent: String,
    val questionImage: ByteArray? = null,
    val answerList: List<Answer>? = null
)
