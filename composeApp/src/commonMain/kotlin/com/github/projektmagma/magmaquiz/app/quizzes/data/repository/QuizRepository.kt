package com.github.projektmagma.magmaquiz.app.quizzes.data.repository

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.home.presentation.model.main.HomeScreenState
import com.github.projektmagma.magmaquiz.app.quizzes.data.service.QuizService
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
    
    val cachedQuizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizListQuizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val userDetailsQuizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val homeState = MutableStateFlow(HomeScreenState())
    
    suspend fun getQuizById(id: UUID): Resource<Quiz, NetworkError> {
        return quizService.getQuizById(id)
    }
    
    suspend fun getQuiz(name: String = "", count: Int = 5, offset: Int): Resource<List<Quiz>, NetworkError> {
        return quizService.getQuizByName(name, count, offset * count)
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
        updateAllLists { 
            it.changeLikeStatusInList(id)
        }

        return quizService.changeFavoriteStatus(id)
    }
    
    suspend fun getMyGameHistory(count: Int = 10, offset: Int): Resource<List<Quiz>, NetworkError>{
        return quizService.getMyGameHistory(count, offset * count)
    }

    suspend fun getQuizzesByUserId(id: UUID, count: Int = 10, offset: Int): Resource<List<Quiz>, NetworkError> {
        return quizService.getQuizzesByUserId(id, count, offset * count)
    }

    suspend fun deleteQuiz(id: UUID): Resource<Unit, NetworkError> {
        return quizService.deleteQuiz(id)
    }
    
    fun deleteQuizInList(id: UUID){
        updateAllLists { 
            it.deleteQuizInList(id)
        }
    }

    suspend fun getMyFavorites(name: String, count: Int = 5, offset: Int): Resource<List<Quiz>, NetworkError> {
        return quizService.getMyFavoritesByName(name, count, offset * count)
    }
    
    suspend fun getRecentlyAddedQuizzes(name: String = "", count: Int = 5, offset: Int): Resource<List<Quiz>, NetworkError> {
        return quizService.getRecentlyAddedQuizzesByName(name, count, offset * count)
    }

    suspend fun getMostLikedQuizzes(name: String = "", count: Int = 5, offset: Int): Resource<List<Quiz>, NetworkError> {
        return quizService.getMostLikedQuizzesByName(name, count, offset * count)
    }

    suspend fun getFriendsQuizzes(name: String = "", count: Int = 5, offset: Int): Resource<List<Quiz>, NetworkError> {
        return quizService.getFriendsQuizzesByName(name, count, offset * count)
    }
    
    suspend fun markQuizAsPlayed(): Resource<Unit, NetworkError>{
        return quizService.markQuizAsPlayed(quiz.value?.id!!)
    }
    
    suspend fun getQuizReviews(uuid: UUID): Resource<List<QuizReview>, NetworkError>{
        return quizService.getQuizReviews(uuid)
    }
    
    suspend fun createQuizReview(uuid: UUID, review: QuizReview): Resource<Unit, NetworkError>{
        val result = quizService.createQuizReview(uuid, review)
        
        updateAllLists { 
            it.changeRating(uuid, review.rating)
            it.changeReviewedStatus(uuid, true)
        }
        return result
    }
    
    suspend fun deleteQuizReview(uuid: UUID, rating: Int): Resource<Unit, NetworkError>{
        val newRating = -rating
        updateAllLists { 
            it.changeRating(uuid, newRating)
            it.changeReviewedStatus(uuid, false)
        }
        
        return quizService.deleteQuizReview(uuid, rating)
    }
    
    suspend fun getTags(name: String = "", count: Int = 5): Resource<List<Tag>, NetworkError>{
        return quizService.getTags(name, count)
    }
    
    fun List<Quiz>.changeReviewedStatus(id: UUID, reviewed: Boolean): List<Quiz>{
        return this.map { quiz -> 
            if (quiz.id == id) {
                quiz.copy(reviewedByYou = reviewed)
            } else {
                quiz
            }
        }
    }
    
    private fun updateAllLists(transform: (List<Quiz>) -> List<Quiz>){
        userDetailsQuizzes.update(transform)
        quizListQuizzes.update(transform)
        cachedQuizzes.update(transform)
        homeState.update { it.copy(
            recentQuizzes = transform(it.recentQuizzes),
            mostLikedQuizzes = transform(it.mostLikedQuizzes),
            friendsQuizzes = transform(it.friendsQuizzes)
        ) }
    }

    private fun List<Quiz>.changeLikeStatusInList(id: UUID): List<Quiz>{
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

    private fun List<Quiz>.changeRating(id: UUID, rate: Int): List<Quiz> {
        return this.map { quiz ->
            val reviewDigit = if (rate > 0) 1 else -1
            val reviewCount = quiz.reviewCount + reviewDigit
            if (quiz.id == id) {
                quiz.copy(
                    reviewCount = reviewCount,
                    averageRating = (quiz.averageRating + rate) / reviewCount.coerceAtLeast(1),
                )
            } else {
                quiz
            }
        }
    }
    
    private fun List<Quiz>.deleteQuizInList(id: UUID): List<Quiz> {
        return this.filter { it.id != id }
    }
}