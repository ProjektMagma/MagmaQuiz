package com.github.projektmagma.magmaquiz.shared.data.rest.values

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmEmailChangeValue(
    val email: String,
    val verificationCode: String
)