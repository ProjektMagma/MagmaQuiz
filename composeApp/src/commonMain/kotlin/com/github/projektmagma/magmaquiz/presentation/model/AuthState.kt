package com.github.projektmagma.magmaquiz.presentation.model

import com.github.projektmagma.magmaquiz.domain.validator.EmailError
import com.github.projektmagma.magmaquiz.domain.validator.PasswordError
import com.github.projektmagma.magmaquiz.domain.validator.RepeatedPasswordError

data class AuthState(
    val username: String = "",
    val email: String = "",
    val emailError: EmailError? = null,
    val password: String = "",
    val passwordError: PasswordError? = null,
    val repeatedPassword: String = "",
    val repeatedPasswordError: RepeatedPasswordError? = null
)