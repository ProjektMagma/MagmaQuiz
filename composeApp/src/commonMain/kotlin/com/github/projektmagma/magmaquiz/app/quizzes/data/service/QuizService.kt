package com.github.projektmagma.magmaquiz.app.quizzes.data.service

import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.util.UUID

class QuizService(
    private val httpClient: HttpClient,
) {
    suspend fun getQuizByName(name: String): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/find/$name")
        }
    }

    suspend fun getQuizById(id: UUID): Resource<Quiz, NetworkError> {
        return safeCall<Quiz> {
            httpClient.get("quiz/$id")
        }
    }

    suspend fun changeFavoriteStatus(id: UUID): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.get("quiz/changeFavoriteStatus/$id")
        }
    }

    suspend fun getMyFavorites(): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/myFavorites")
        }
    }

    suspend fun createQuiz(quiz: CreateOrModifyQuizValue): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.post("/quiz/create") {
                contentType(ContentType.Application.Json)
                setBody(quiz)
            }
        }
    }

    suspend fun modifyQuiz(quiz: CreateOrModifyQuizValue): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.post("quiz/modify") {
                contentType(ContentType.Application.Json)
                setBody(quiz)
            }
        }
    }

    suspend fun getQuizzesByUserId(id: UUID): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/findByUser/$id")
        }
    }

    suspend fun deleteQuiz(id: UUID): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.delete("quiz/$id")
        }
    }

    suspend fun getMostLikedQuizzes(count: Long): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/mostLiked/${count}")
        }
    }

    suspend fun getFriendsQuizzes(count: Long): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/friendsQuizzes/")
//            TODO: Tutaj trzeba by zrobić też count, ale to najpierw serwer musi to mieć
//            httpClient.get("quiz/friendsQuizzes/${count}")
        }
    }

    suspend fun getRecentlyAddedQuizzes(count: Long): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/newest/${count}")
        }
    }
}