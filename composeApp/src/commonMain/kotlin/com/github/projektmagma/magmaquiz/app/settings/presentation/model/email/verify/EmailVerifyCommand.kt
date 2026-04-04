package com.github.projektmagma.magmaquiz.app.settings.presentation.model.email.verify

sealed interface EmailVerifyCommand {
    data class ChangeVerificationCode(val newCode: String) : EmailVerifyCommand
    data object ResendCode : EmailVerifyCommand
    data object VerifyCode : EmailVerifyCommand
    data object SendVerificationEmail : EmailVerifyCommand
}