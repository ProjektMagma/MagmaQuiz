package com.github.projektmagma.magmaquiz.shared.data.rest.values

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordValue(
    val newPassword: String
)
