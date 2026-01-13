package com.github.projektmagma.magmaquiz.app.home.presentation.model.settings

sealed interface SettingsCommand {

    data class ImageChanged(val profilePicture: ByteArray?) : SettingsCommand
    data object ChangeProfilePicture : SettingsCommand
}