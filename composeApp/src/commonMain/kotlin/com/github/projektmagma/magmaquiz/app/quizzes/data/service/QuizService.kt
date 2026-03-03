package com.github.projektmagma.magmaquiz.app.quizzes.data.service

import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizReview
import com.github.projektmagma.magmaquiz.shared.data.domain.Tag
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class QuizService(
    private val httpClient: HttpClient,
) {
    suspend fun getQuizById(id: UUID): Resource<Quiz, NetworkError> {
        return safeCall<Quiz> {
            httpClient.get("quiz/$id")
        }
    }
    
    suspend fun getQuizzes(count: Int): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> { 
            httpClient.get("quiz/find/$count")
        }
    }
    
    suspend fun getQuizByName(name: String, count: Int): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/find/$count/$name")
        }
    }

    suspend fun createQuiz(quiz: CreateOrModifyQuizValue): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.post("quiz/create") {
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

    suspend fun changeFavoriteStatus(id: UUID): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.get("quiz/changeFavoriteStatus/$id")
        }
    }

    suspend fun getMyGameHistory(count: Int = 100): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/MyGameHistory/$count")
        }
    }

    suspend fun getQuizzesByUserId(id: UUID, count: Int = 100): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/findByUser/$count/$id")
        }
    }

    suspend fun deleteQuiz(id: UUID): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.delete("quiz/$id")
        }
    }

    suspend fun getMyFavoritesByName(name: String, count: Int): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/myFavorites/$count/$name")
        }
    }

    suspend fun getRecentlyAddedQuizzesByName(name: String, count: Long): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/newest/$count/$name")
        }
    }

    suspend fun getMostLikedQuizzesByName(name: String, count: Long): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/mostLiked/$count/$name")
        }
    }

    suspend fun getFriendsQuizzesByName(name: String, count: Long): Resource<List<Quiz>, NetworkError> {
        return safeCall<List<Quiz>> {
            httpClient.get("quiz/friendsQuizzes/$count/$name")
        }
    }
    
    suspend fun markQuizAsPlayed(uuid: UUID): Resource<Unit, NetworkError>{
        return safeCall<Unit> { 
            httpClient.get("quiz/markAsPlayed/$uuid")
        }
    }
    
    suspend fun getQuizReviews(uuid: UUID): Resource<List<QuizReview>, NetworkError>{
        return safeCall<List<QuizReview>> { 
            httpClient.get("quiz/reviews/$uuid")
        }
    }
    
    suspend fun createQuizReview(uuid: UUID, quizReview: QuizReview): Resource<Unit, NetworkError>{
        return safeCall<Unit> { 
            httpClient.post("quiz/reviews/create/$uuid"){
                setBody(quizReview)
            }
        }
    }
    
    suspend fun deleteQuizReview(uuid: UUID): Resource<Unit, NetworkError>{
        return safeCall<Unit> { 
            httpClient.delete("quiz/reviews/delete/$uuid")
        }
    }

    suspend fun getTags(name: String, count: Int): Resource<List<Tag>, NetworkError>{
        return safeCall<List<Tag>> {
            httpClient.get("quiz/tags/$count/$name")
        }
    }
}