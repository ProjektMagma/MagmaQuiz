package com.github.projektmagma.magmaquiz.shared.data.domain

import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RoomSettings(
    @Serializable(UUIDSerializer::class)
    val roomId: UUID,
    val roomName: String,
    val currentQuiz: Quiz,
    val roomOwner: ForeignUser,
    val questionTimeInMillis: Long,
    val isPublic: Boolean,
    val isInProgress: Boolean,
    var connectedUsers: Int,
    val userList: List<ForeignUser> = emptyList(),
)