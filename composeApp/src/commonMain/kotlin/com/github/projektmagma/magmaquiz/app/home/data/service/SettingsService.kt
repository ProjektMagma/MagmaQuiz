package com.github.projektmagma.magmaquiz.app.home.data.service

import com.github.projektmagma.magmaquiz.app.core.data.ApiDataStore
import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.core.util.BaseUrlProvider
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class SettingsService(
    private val httpClient: HttpClient,
    private val baseUrlProvider: BaseUrlProvider,
    private val apiDataStore: ApiDataStore
) {

    suspend fun postChangeProfilePicture(
        profilePictureBig: ByteArray,
        profilePictureSmall: ByteArray
    ): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.post("${baseUrlProvider.getBaseUrl()}/settings/change/profilePicture") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
                setBody(ChangeProfilePictureValue(profilePictureBig, profilePictureSmall))
            }
        }
    }
}