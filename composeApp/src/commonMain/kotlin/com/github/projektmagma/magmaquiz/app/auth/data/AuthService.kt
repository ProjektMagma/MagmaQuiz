package com.github.projektmagma.magmaquiz.app.auth.data

import com.github.projektmagma.magmaquiz.app.core.data.ApiDataStore
import com.github.projektmagma.magmaquiz.app.core.data.ServerConfigDataStore
import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.ThisUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateUserValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.LoginUserValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthService(
    private val httpClient: HttpClient,
    private val apiDataStore: ApiDataStore,
    private val serverConfigStore: ServerConfigDataStore
) {
    private suspend fun getBaseUrl(): String {
        val config = serverConfigStore.getServerConfig()
        return "${config.protocol.name}://${config.ip}:${config.port}"
    }

    suspend fun registerUser(username: String, email: String, password: String): Resource<ThisUser, NetworkError> {
        return safeCall<ThisUser> {
            val result = httpClient.post("${getBaseUrl()}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(CreateUserValue(username, email, password))
            }
            apiDataStore.setSessionHeader(result.headers["user_session"] ?: "")
            result
        }
    }

    suspend fun loginUser(email: String, password: String): Resource<ThisUser, NetworkError> {
        return safeCall<ThisUser> {
            val result = httpClient.post("${getBaseUrl()}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginUserValue(email, password))
            }
            apiDataStore.setSessionHeader(result.headers["user_session"] ?: "")
            result
        }
    }

    suspend fun whoAmI(): Resource<ThisUser, NetworkError> {
        return safeCall<ThisUser> {
            val result = httpClient.get("${getBaseUrl()}/auth/whoami") {
                header("user_session", apiDataStore.getSessionHeader())
            }
            println("TAG: $result")
            result
        }
    }

    suspend fun logoutUser(): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            val result = httpClient.get("${getBaseUrl()}/auth/logout") {
                header("user_session", apiDataStore.getSessionHeader())
            }
            apiDataStore.setSessionHeader("")
            result
        }
    }
}