package com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.change

import com.github.projektmagma.magmaquiz.app.auth.domain.validator.PasswordError

data class PasswordChangeState(
    val oldPassword: String = "",
    val oldPasswordError: PasswordError? = null,
    val newPassword: String = "",
    val newPasswordError: PasswordError? = null,
    val repeatedPassword: String = "",
    val passwordMatch: Boolean? = null
)