package com.github.projektmagma.magmaquiz.shared.data.domain

import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserAnswer(
    @Serializable(UUIDSerializer::class)
    val userId: UUID,
    @Serializable(UUIDSerializer::class)
    val answerId: UUID
)