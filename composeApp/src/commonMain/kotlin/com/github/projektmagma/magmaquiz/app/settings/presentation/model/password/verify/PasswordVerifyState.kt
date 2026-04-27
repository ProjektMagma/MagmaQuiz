package com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.verify

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError

data class PasswordVerifyState(
    val code: String = "",
    val email: String = "",
    val remainingSeconds: Int = 15,
    val codeError: NetworkError? = null
)