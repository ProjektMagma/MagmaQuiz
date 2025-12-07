package com.github.projektmagma.magmaquiz.server.data.util

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val userId: Int, val userName: String)