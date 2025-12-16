package com.github.projektmagma.magmaquiz.data

import com.github.projektmagma.magmaquiz.data.domain.User
import com.github.projektmagma.magmaquiz.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.data.networking.safeCall
import com.github.projektmagma.magmaquiz.data.rest.values.CreateUserValue
import com.github.projektmagma.magmaquiz.data.rest.values.LoginUserValue
import com.github.projektmagma.magmaquiz.domain.NetworkError
import io.ktor.client.*
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserRepository(
    private val httpClient: HttpClient
) {
    suspend fun registerUser(username: String, email: String, password: String) : Resource<User, NetworkError>{
        return safeCall<User> { 
            httpClient.post("http://localhost:8080/user/register") {
                contentType(ContentType.Application.Json)
                setBody(CreateUserValue(username, email, password))
            }
        }
    }

    suspend fun loginUser(email: String, password: String) : Resource<User, NetworkError> {
        return safeCall<User> { 
            httpClient.post("http://localhost:8080/user/login") { 
                contentType(ContentType.Application.Json)
                setBody(LoginUserValue(email, password))
            }
        }
    }
}