package com.github.projektmagma.magmaquiz.data.rest.values

import kotlinx.serialization.Serializable

@Serializable
data class LoginUserValue(
    val userEmail: String,
    val userPassword: String,
)
