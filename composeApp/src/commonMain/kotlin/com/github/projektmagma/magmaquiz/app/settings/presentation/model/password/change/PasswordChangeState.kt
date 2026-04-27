package com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.change

import com.github.projektmagma.magmaquiz.app.auth.domain.validator.PasswordError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error

data class PasswordChangeState(
    val oldPassword: String = "",
    val oldPasswordError: Error? = null,
    val newPassword: String = "",
    val newPasswordError: PasswordError? = null,
    val repeatedPassword: String = "",
    val passwordMatch: Boolean? = null
)