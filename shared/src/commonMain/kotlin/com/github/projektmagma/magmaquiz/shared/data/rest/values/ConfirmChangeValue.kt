package com.github.projektmagma.magmaquiz.shared.data.rest.values

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmChangeValue(
    val payload: String,
    val verificationCode: String
)