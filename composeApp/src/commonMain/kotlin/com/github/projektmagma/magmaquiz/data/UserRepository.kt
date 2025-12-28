package com.github.projektmagma.magmaquiz.data

import com.github.projektmagma.magmaquiz.data.domain.ThisUser
import com.github.projektmagma.magmaquiz.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.data.networking.safeCall
import com.github.projektmagma.magmaquiz.data.rest.values.CreateUserValue
import com.github.projektmagma.magmaquiz.data.rest.values.LoginUserValue
import com.github.projektmagma.magmaquiz.domain.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow

class UserRepository(
    private val httpClient: HttpClient
) {

    private val url = "http://10.0.2.2:8080" // TODO: MOCNO TYMCZASOWE

    val thisUser = MutableStateFlow<ThisUser?>(null)

    suspend fun registerUser(username: String, email: String, password: String): Resource<ThisUser, NetworkError> {
        return safeCall<ThisUser> {
            httpClient.post("${url}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(CreateUserValue(username, email, password))
            }
        }
    }

    suspend fun loginUser(email: String, password: String): Resource<ThisUser, NetworkError> {
        return safeCall<ThisUser> {
            httpClient.post("${url}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginUserValue(email, password))
            }
        }
    }
    
    suspend fun whoAmI(): Resource<ThisUser, NetworkError>{
        return safeCall<ThisUser> { 
            val result = httpClient.get("${url}/auth/whoami")
            println("TAG: $result")
            result
        }
    }

    suspend fun logoutUser(): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.get("${url}/settings/logout")
        }
    }
}