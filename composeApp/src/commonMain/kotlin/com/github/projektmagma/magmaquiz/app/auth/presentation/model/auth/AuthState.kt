package com.github.projektmagma.magmaquiz.app.auth.presentation.model.auth

import com.github.projektmagma.magmaquiz.app.auth.domain.validator.EmailError
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.PasswordError
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.UsernameError

data class AuthState(
    val username: String = "",
    val usernameError: UsernameError? = null,
    val email: String = "",
    val emailError: EmailError? = null,
    val password: String = "",
    val passwordError: PasswordError? = null,
)