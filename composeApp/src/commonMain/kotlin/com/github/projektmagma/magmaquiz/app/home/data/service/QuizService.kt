package com.github.projektmagma.magmaquiz.app.home.data.service

import com.github.projektmagma.magmaquiz.app.core.data.ApiDataStore
import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.core.util.BaseUrlProvider
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.util.UUID

class QuizService(
    private val httpClient: HttpClient,
    private val baseUrlProvider: BaseUrlProvider,
    private val apiDataStore: ApiDataStore,
) {
    suspend fun getQuizByName(name: String): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("${baseUrlProvider.getBaseUrl()}/quiz/find/$name") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }

    suspend fun getQuizById(id: UUID): Resource<Quiz, NetworkError> {
        return safeCall<Quiz> {
            httpClient.get("${baseUrlProvider.getBaseUrl()}/quiz/$id") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }

    suspend fun changeFavoriteStatus(id: UUID): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.get("${baseUrlProvider.getBaseUrl()}/quiz/changeFavoriteStatus/$id") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }

    suspend fun getMyFavorites(): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("${baseUrlProvider.getBaseUrl()}/quiz/myFavorites") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }

    suspend fun createQuiz(quiz: CreateOrModifyQuizValue): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.post("${baseUrlProvider.getBaseUrl()}/quiz/create") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
                setBody(quiz)
            }
        }
    }
    
    suspend fun modifyQuiz(quiz: CreateOrModifyQuizValue): Resource<Unit, NetworkError> {
        return safeCall<Unit> { 
            httpClient.post("${baseUrlProvider.getBaseUrl()}/quiz/modify") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
                setBody(quiz)
            }
        }
    }

    suspend fun getQuizzesByUserId(id: UUID): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("${baseUrlProvider.getBaseUrl()}/quiz/findByUser/$id") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }
    
    suspend fun deleteQuiz(id: UUID): Resource<Unit, NetworkError> {
        return safeCall<Unit> { 
            httpClient.delete("${baseUrlProvider.getBaseUrl()}/quiz/$id"){
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }
}