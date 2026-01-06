package com.github.projektmagma.magmaquiz.app.auth.data

import com.github.projektmagma.magmaquiz.app.core.data.ApiDataStore
import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.core.util.BaseUrlProvider
import com.github.projektmagma.magmaquiz.shared.data.domain.ThisUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateUserValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.LoginUserValue
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthService(
    private val httpClient: HttpClient,
    private val apiDataStore: ApiDataStore,
    private val baseUrlProvider: BaseUrlProvider
) {
    suspend fun registerUser(username: String, email: String, password: String): Resource<ThisUser, NetworkError> {
        return safeCall<ThisUser> {
            val result = httpClient.post("${baseUrlProvider.getBaseUrl()}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(CreateUserValue(username, email, password))
            }
            apiDataStore.setSessionHeader(result.headers["user_session"] ?: "")
            result
        }
    }

    suspend fun loginUser(email: String, password: String): Resource<ThisUser, NetworkError> {
        return safeCall<ThisUser> {
            val result = httpClient.post("${baseUrlProvider.getBaseUrl()}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginUserValue(email, password))
            }
            apiDataStore.setSessionHeader(result.headers["user_session"] ?: "")
            result
        }
    }

    suspend fun whoAmI(): Resource<ThisUser, NetworkError> {
        return safeCall<ThisUser> {
            httpClient.get("${baseUrlProvider.getBaseUrl()}/auth/whoami") {
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }

    suspend fun logoutUser(): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            val result = httpClient.get("${baseUrlProvider.getBaseUrl()}/auth/logout") {
                header("user_session", apiDataStore.getSessionHeader())
            }
            apiDataStore.setSessionHeader("")
            result
        }
    }
}