package com.github.projektmagma.magmaquiz.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Int? = null,
    val userName: String,
    val userEmail: String,
    val mustChangePassword: Boolean,
    val userProfilePicture: ByteArray? = null
)