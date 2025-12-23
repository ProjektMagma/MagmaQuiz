package com.github.projektmagma.magmaquiz.data

import com.github.projektmagma.magmaquiz.data.domain.User
import com.github.projektmagma.magmaquiz.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.data.networking.safeCall
import com.github.projektmagma.magmaquiz.data.rest.values.CreateUserValue
import com.github.projektmagma.magmaquiz.data.rest.values.LoginUserValue
import com.github.projektmagma.magmaquiz.domain.NetworkError
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow

class UserRepository(
    private val httpClient: HttpClient
) {

    private val url = "http://192.168.1.149:8080" // TODO: MOCNO TYMCZASOWE

    val user = MutableStateFlow<User?>(null)

    suspend fun registerUser(username: String, email: String, password: String): Resource<User, NetworkError> {
        return safeCall<User> {
            httpClient.post("${url}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(CreateUserValue(username, email, password))
            }
        }
    }

    suspend fun loginUser(email: String, password: String): Resource<User, NetworkError> {
        return safeCall<User> {
            httpClient.post("${url}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginUserValue(email, password))
            }
        }
    }

    suspend fun logoutUser(): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.get("${url}/auth/logout")
        }
    }
}