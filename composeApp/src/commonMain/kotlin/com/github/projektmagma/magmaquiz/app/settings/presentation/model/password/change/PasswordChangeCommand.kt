package com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.change

sealed interface PasswordChangeCommand {
    data class OldPasswordChanged(val password: String): PasswordChangeCommand
    data class NewPasswordChanged(val password: String): PasswordChangeCommand
    data class RepeatedPasswordChange(val password: String): PasswordChangeCommand
    data object Save: PasswordChangeCommand
}