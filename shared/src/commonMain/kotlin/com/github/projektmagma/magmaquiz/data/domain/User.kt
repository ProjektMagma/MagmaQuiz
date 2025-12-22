package com.github.projektmagma.magmaquiz.data.domain

import com.github.projektmagma.magmaquiz.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class User(
    @Serializable(UUIDSerializer::class)
    val userId: UUID? = null,
    val userName: String,
    val userEmail: String,
    val mustChangePassword: Boolean,
    val userProfilePicture: ByteArray? = null
)