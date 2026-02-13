package com.github.projektmagma.magmaquiz.app.settings.data.service

import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

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
}