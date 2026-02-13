package com.github.projektmagma.magmaquiz.app.settings.presentation.model

sealed interface SettingsCommand {

    data class ImageChanged(val profilePicture: ByteArray?) : SettingsCommand
    data object ChangeProfilePicture : SettingsCommand
}