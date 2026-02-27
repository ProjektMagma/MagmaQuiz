package com.github.projektmagma.magmaquiz.app.quizzes.data.repository

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.game.presentation.model.GameState
import com.github.projektmagma.magmaquiz.app.quizzes.data.service.QuizService
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizListState
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.CreateQuizState
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizReview
import com.github.projektmagma.magmaquiz.shared.data.domain.Tag
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class QuizRepository(
    private val quizService: QuizService
) {
    val quiz = MutableStateFlow<Quiz?>(null)

    val gameState = MutableStateFlow(GameState())
    val quizListState = MutableStateFlow(QuizListState())

    val createQuizState = MutableStateFlow(CreateQuizState())
    
    val userDetailsQuizList = MutableStateFlow<List<Quiz>?>(emptyList())

    suspend fun getQuizById(id: UUID): Resource<Quiz, NetworkError> {
        return quizService.getQuizById(id)
    }
    
    suspend fun getQuiz(name: String = "", count: Int = 10): Resource<List<Quiz>, NetworkError> {
        return quizService.getQuizByName(name, count)
    }

    suspend fun createQuiz(quiz: CreateOrModifyQuizValue): Resource<Unit, NetworkError> {
        return quizService.createQuiz(quiz)
    }

    suspend fun modifyQuiz(quiz: CreateOrModifyQuizValue): Resource<Unit, NetworkError> {
        return quizService.modifyQuiz(quiz)
    }

    suspend fun changeFavoriteStatus(
        id: UUID
    ): Resource<Unit, NetworkError> {
        quizListState.update {
            it.copy(quizzes = it.quizzes.changeLikeStatusInList(id))
        }
        userDetailsQuizList.value = userDetailsQuizList.value?.changeLikeStatusInList(id)

        return quizService.changeFavoriteStatus(id)
    }
    
    suspend fun getMyGameHistory(): Resource<List<Quiz>, NetworkError>{
        return quizService.getMyGameHistory()
    }

    suspend fun getQuizzesByUserId(id: UUID): Resource<List<Quiz>, NetworkError> {
        return quizService.getQuizzesByUserId(id)
    }

    suspend fun deleteQuiz(id: UUID): Resource<Unit, NetworkError> {
        return quizService.deleteQuiz(id)
    }

    suspend fun getMyFavorites(name: String, count: Int = 10): Resource<List<Quiz>, NetworkError> {
        return quizService.getMyFavoritesByName(name, count)
    }
    
    suspend fun getRecentlyAddedQuizzes(name: String = "", count: Long = 100): Resource<List<Quiz>, NetworkError> {
        return quizService.getRecentlyAddedQuizzesByName(name, count)
    }

    suspend fun getMostLikedQuizzes(name: String = "", count: Long = 100): Resource<List<Quiz>, NetworkError> {
        return quizService.getMostLikedQuizzesByName(name, count)
    }

    suspend fun getFriendsQuizzes(name: String = "", count: Long = 100): Resource<List<Quiz>, NetworkError> {
        return quizService.getFriendsQuizzesByName(name, count)
    }
    
    suspend fun markQuizAsPlayed(uuid: UUID): Resource<Unit, NetworkError>{
        return quizService.markQuizAsPlayed(uuid)
    }
    
    suspend fun getQuizReviews(uuid: UUID): Resource<List<QuizReview>, NetworkError>{
        return quizService.getQuizReviews(uuid)
    }
    
    suspend fun createQuizReview(uuid: UUID, review: QuizReview): Resource<Unit, NetworkError>{
        return quizService.createQuizReview(uuid, review)
    }
    
    suspend fun deleteQuizReview(uuid: UUID): Resource<Unit, NetworkError>{
        return quizService.deleteQuizReview(uuid)
    }
    
    suspend fun getTags(name: String = "", count: Int = 10): Resource<List<Tag>, NetworkError>{
        return quizService.getTags(name, count)
    }

    fun List<Quiz>.changeLikeStatusInList(id: UUID): List<Quiz>{
        return this.map { quiz ->
            if (quiz.id == id) {
                quiz.copy(
                    likedByYou = !quiz.likedByYou,
                    likesCount = if (quiz.likedByYou) quiz.likesCount - 1 else quiz.likesCount + 1
                )
            } else {
                quiz
            }
        }
    }
}