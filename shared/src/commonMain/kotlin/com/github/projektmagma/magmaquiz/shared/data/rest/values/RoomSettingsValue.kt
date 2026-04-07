package com.github.projektmagma.magmaquiz.shared.data.rest.values

import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RoomSettingsValue(
    val roomName: String,
    @Serializable(UUIDSerializer::class)
    val quizId: UUID,
    val questionTimeInMillis: Long
)
