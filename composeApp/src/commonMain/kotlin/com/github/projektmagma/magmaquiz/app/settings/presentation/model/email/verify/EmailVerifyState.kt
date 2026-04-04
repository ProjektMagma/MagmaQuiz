package com.github.projektmagma.magmaquiz.app.settings.presentation.model.email.verify

data class EmailVerifyState(
    val code: String = "",
    val timeToResend: Int = 15
)