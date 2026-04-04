package com.github.projektmagma.magmaquiz.app.auth.presentation.model.auth

import com.github.projektmagma.magmaquiz.app.auth.domain.validator.PasswordError
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.UsernameError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error

data class AuthState(
    val username: String = "",
    val usernameError: UsernameError? = null,
    val email: String = "",
    val emailError: Error? = null,
    val password: String = "",
    val passwordError: PasswordError? = null,
)