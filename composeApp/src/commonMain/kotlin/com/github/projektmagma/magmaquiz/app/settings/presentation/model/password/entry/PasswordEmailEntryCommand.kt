package com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.entry

sealed interface PasswordEmailEntryCommand {
    data class EmailChanged(val newEmail: String): PasswordEmailEntryCommand
    data object CheckEmail: PasswordEmailEntryCommand
}