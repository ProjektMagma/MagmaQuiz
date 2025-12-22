package com.github.projektmagma.magmaquiz.data.domain

import com.github.projektmagma.magmaquiz.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Answer(
    @Serializable(UUIDSerializer::class)
    val id: UUID? = null,
    val answerContent: String,
    val isCorrect: Boolean
)
