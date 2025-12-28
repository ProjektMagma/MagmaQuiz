package com.github.projektmagma.magmaquiz.data

import com.github.projektmagma.magmaquiz.data.domain.ThisUser
import com.github.projektmagma.magmaquiz.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.data.networking.safeCall
import com.github.projektmagma.magmaquiz.data.rest.values.CreateUserValue
import com.github.projektmagma.magmaquiz.data.rest.values.LoginUserValue
import com.github.projektmagma.magmaquiz.domain.NetworkError
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthService(
    private val httpClient: HttpClient,
    private val apiDataStore: ApiDataStore

) {
    private val url = "http://192.168.1.149:8080" // TODO: MOCNO TYMCZASOWE

    suspend fun registerUser(username: String, email: String, password: String): Resource<ThisUser, NetworkError> {
        return safeCall<ThisUser> {
            val result = httpClient.post("${url}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(CreateUserValue(username, email, password))
            }
            apiDataStore.setSessionHeader(result.headers["user_session"] ?: "")
            result
        }
    }

    suspend fun loginUser(email: String, password: String): Resource<ThisUser, NetworkError> {
        return safeCall<ThisUser> {
            val result = httpClient.post("${url}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginUserValue(email, password))
            }
            apiDataStore.setSessionHeader(result.headers["user_session"] ?: "")
            result
        }
    }

    suspend fun whoAmI(): Resource<ThisUser, NetworkError> {
        return safeCall<ThisUser> {
            val result = httpClient.get("${url}/auth/whoami") {
                header("user_session", apiDataStore.getSessionHeader())
            }
            println("TAG: $result")
            result
        }
    }

    suspend fun logoutUser(): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            val result = httpClient.get("${url}/auth/logout") {
                header("user_session", apiDataStore.getSessionHeader())
            }
            apiDataStore.setSessionHeader("")
            result
        }
    }
}