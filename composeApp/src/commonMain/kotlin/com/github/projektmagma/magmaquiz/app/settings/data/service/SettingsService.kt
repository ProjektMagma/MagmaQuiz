package com.github.projektmagma.magmaquiz.app.settings.data.service

import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangePasswordWithOldValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ConfirmChangeValue
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class SettingsService(
    private val httpClient: HttpClient,
) {

    suspend fun postChangeProfilePicture(
        profilePictureBig: ByteArray,
        profilePictureSmall: ByteArray
    ): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.post("settings/change/profilePicture") {
                contentType(ContentType.Application.Json)
                setBody(ChangeProfilePictureValue(profilePictureBig, profilePictureSmall))
            }
        }
    }

    suspend fun changeCountryCode(countryCode: String): Resource<Unit, NetworkError>{
        return safeCall<Unit> { 
            httpClient.get("settings/change/countryCode/$countryCode")
        }
    }
    
    suspend fun changeTown(town: String): Resource<Unit, NetworkError>{
        return safeCall<Unit> { 
            httpClient.get("settings/change/town/$town")
        }
    }

    suspend fun changeEmail(email: String): Resource<Unit, NetworkError>{
        return safeCall<Unit> { 
            httpClient.get("settings/change/email/$email")
        }
    }

    suspend fun changeBio(bio: String): Resource<Unit, NetworkError>{
        return safeCall<Unit> {
            httpClient.get("settings/change/bio/$bio")
        }
    }

    suspend fun changeUsername(username: String): Resource<Unit, NetworkError>{
        return safeCall<Unit> {
            httpClient.get("settings/change/userName/$username")
        }
    }
    
    suspend fun sendVerificationCode(email: String): Resource<Unit, NetworkError> {
        return safeCall<Unit> { 
            httpClient.post("settings/verificationCode/$email")
        }
    }
    
    suspend fun checkIsEmailTaken(email: String): Resource<Unit, NetworkError> {
        return safeCall<Unit> { 
            httpClient.get("settings/change/email/checkIsTaken/$email")
        }
    }
    
    suspend fun confirmChangeEmail(email: String, verificationCode: String): Resource<Unit, NetworkError> {
        return safeCall<Unit> { 
            httpClient.post("settings/change/email/confirm") {
                contentType(ContentType.Application.Json)
                setBody(ConfirmChangeValue(email = email, payload =  verificationCode))
            }
        }
    }
    
    suspend fun verifyPasswordCode(email: String, verificationCode: String): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.post("settings/change/password/verifyCode") { 
                contentType(ContentType.Application.Json)
                setBody(ConfirmChangeValue(email,verificationCode))
            }
        }
    }
    
    suspend fun changePassword(email: String, password: String): Resource<Unit, NetworkError> {
        return safeCall<Unit> { 
            httpClient.post("settings/change/password/new") { 
                contentType(ContentType.Application.Json)
                setBody(ConfirmChangeValue(email, password))
            }
        }
    }
    
    suspend fun changePasswordWithOld(oldPassword: String, newPassword: String): Resource<Unit, NetworkError> {
        return safeCall<Unit> { 
            httpClient.post("settings/change/password/old") { 
                contentType(ContentType.Application.Json)
                setBody(ChangePasswordWithOldValue(oldPassword, newPassword))
            }
        }
    }
    
    suspend fun deleteAccount(): Resource<Unit, NetworkError> {
        return safeCall<Unit> { 
            httpClient.delete("settings/deleteAccount")
        }
    }
}