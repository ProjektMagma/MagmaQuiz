package com.github.projektmagma.magmaquiz.shared.data.domain

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User
import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ForeignUser(
    @Serializable(UUIDSerializer::class)
    override val userId: UUID? = null,
    override val userName: String,
    override val userProfilePicture: ByteArray? = null,
    override val createdAt: Long,
    override val lastActivity: Long,
    val friendshipStatus: FriendshipStatus
) : User

