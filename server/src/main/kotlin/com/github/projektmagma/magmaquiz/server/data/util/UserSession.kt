package com.github.projektmagma.magmaquiz.server.data.util

import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserSession(
    @Serializable(UUIDSerializer::class)
    val userId: UUID,
    val userName: String
)