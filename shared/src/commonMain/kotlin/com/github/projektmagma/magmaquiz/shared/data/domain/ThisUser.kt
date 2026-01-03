package com.github.projektmagma.magmaquiz.shared.data.domain

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User
import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ThisUser(
    @Serializable(UUIDSerializer::class)
    override val userId: UUID? = null,
    override val userName: String,
    val userEmail: String,
    val mustChangePassword: Boolean,
    override val userProfilePicture: ByteArray? = null,
    override val createdAt: Long,
    override val lastActivity: Long
) : User