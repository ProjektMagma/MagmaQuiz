package com.github.projektmagma.magmaquiz.shared.data.rest.values

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserValue(
    val userName: String,
    val userEmail: String,
    val userPassword: String,
)
