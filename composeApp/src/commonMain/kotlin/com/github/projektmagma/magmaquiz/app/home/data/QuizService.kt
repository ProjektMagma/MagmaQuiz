package com.github.projektmagma.magmaquiz.app.home.data

import com.github.projektmagma.magmaquiz.app.core.data.ApiDataStore
import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.core.util.BaseUrlProvider
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class QuizService(
    private val httpClient: HttpClient,
    private val baseUrlProvider: BaseUrlProvider,
    private val apiDataStore: ApiDataStore
) {
    suspend fun getQuizByName(name: String): Resource<List<Quiz>, NetworkError>{
        return safeCall<List<Quiz>> { 
            val result = httpClient.get("${baseUrlProvider.getBaseUrl()}/quiz/find/$name") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
            println("TAG ${result.bodyAsText()}")
            result
        }
    }
}