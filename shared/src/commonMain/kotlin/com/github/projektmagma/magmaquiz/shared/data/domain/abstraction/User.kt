package com.github.projektmagma.magmaquiz.shared.data.domain.abstraction

import java.util.*

interface User {
    val userId: UUID?
    val userName: String
    val userProfilePicture: ByteArray?
    val createdAt: Long
    val lastActivity: Long
    val userBio: String
    val userCountryCode: String
    val userTown: String
}