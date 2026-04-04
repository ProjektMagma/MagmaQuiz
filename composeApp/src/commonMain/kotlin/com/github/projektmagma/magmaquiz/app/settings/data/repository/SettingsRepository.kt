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
    
    suspend fun changeTown(town: String): Resource<Unit, NetworkError> {
        return settingsService.changeTown(town)
    }
    
    suspend fun changeCountryCode(countryCode: String): Resource<Unit, NetworkError> {
        return settingsService.changeCountryCode(countryCode)  
    }
    
    suspend fun changeEmail(email: String): Resource<Unit, NetworkError>{
        return settingsService.changeEmail(email)   
    }
    
    suspend fun changeBio(bio: String): Resource<Unit, NetworkError>{
        return settingsService.changeBio(bio)
    }
    
    suspend fun changeUsername(username: String): Resource<Unit, NetworkError>{
        return settingsService.changeUsername(username)
    }

    suspend fun sendVerificationCode(email: String): Resource<Unit, NetworkError> {
        return settingsService.sendVerificationCode(email)
    }

    suspend fun checkIsEmailTaken(email: String): Resource<Unit, NetworkError> {
        return settingsService.checkIsEmailTaken(email)
    }

    suspend fun confirmChangeEmail(email: String, verificationCode: String): Resource<Unit, NetworkError> {
        return settingsService.confirmChangeEmail(email, verificationCode)
    }
}