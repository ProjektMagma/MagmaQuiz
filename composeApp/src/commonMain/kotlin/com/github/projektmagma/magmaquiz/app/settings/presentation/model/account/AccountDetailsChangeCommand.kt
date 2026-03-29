package com.github.projektmagma.magmaquiz.app.settings.presentation.model.account

sealed interface AccountDetailsChangeCommand {
    data class EmailChanged(val newEmail: String): AccountDetailsChangeCommand
    data class BioChanged(val newBio: String): AccountDetailsChangeCommand
    data class UsernameChanged(val newUsername: String): AccountDetailsChangeCommand
    data object Save: AccountDetailsChangeCommand
}