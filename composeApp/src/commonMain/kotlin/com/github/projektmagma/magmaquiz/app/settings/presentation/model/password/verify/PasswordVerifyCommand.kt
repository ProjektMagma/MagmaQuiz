package com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.verify

sealed interface PasswordVerifyCommand {
    data class CodeChanged(val newCode: String): PasswordVerifyCommand
    data object SendCode: PasswordVerifyCommand
    data object Verify: PasswordVerifyCommand
}