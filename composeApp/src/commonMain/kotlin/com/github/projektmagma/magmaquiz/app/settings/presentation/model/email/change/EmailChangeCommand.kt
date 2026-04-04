package com.github.projektmagma.magmaquiz.app.settings.presentation.model.email.change

sealed interface EmailChangeCommand {
    data class ChangeEmail(val newEmail: String) : EmailChangeCommand
    data object CheckIsEmailTaken : EmailChangeCommand
}