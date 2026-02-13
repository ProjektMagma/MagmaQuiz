package com.github.projektmagma.magmaquiz.app.settings.data.repository

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.settings.data.service.SettingsService
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource

class SettingsRepository(
    private val settingsService: SettingsService
) {

    suspend fun changeProfilePicture(
        profilePictureBig: ByteArray,
        profilePictureSmall: ByteArray
    ): Resource<Unit, NetworkError> {
        return settingsService.postChangeProfilePicture(profilePictureBig, profilePictureSmall)
    }
}