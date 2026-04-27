package com.github.projektmagma.magmaquiz.shared.data.rest.values

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordWithOldValue(
    val oldPassword: String,
    val newPassword: String
)