package com.github.projektmagma.magmaquiz.app.home.data.repository

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.home.data.service.QuizService
import com.github.projektmagma.magmaquiz.app.home.presentation.model.game.GameState
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizListState
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.create.CreateQuizState
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
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
    
    val userDetailsQuizList = MutableStateFlow<List<Quiz>>(emptyList())

    suspend fun getQuizByName(name: String): Resource<List<Quiz>, NetworkError> {
        return quizService.getQuizByName(name)
    }

    suspend fun getQuizById(id: UUID): Resource<Quiz, NetworkError> {
        return quizService.getQuizById(id)
    }

    suspend fun changeFavoriteStatus(
        id: UUID
    ): Resource<Unit, NetworkError> {
        quizListState.update {
            it.copy(
                quizzes = it.quizzes.changeLikeStatusInList(id)
            )
        }
        
        userDetailsQuizList.value = userDetailsQuizList.value.changeLikeStatusInList(id)

        return quizService.changeFavoriteStatus(id)
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

suspend fun getMyFavorites(): Resource<List<Quiz>, NetworkError> {
    return quizService.getMyFavorites()
}

suspend fun createQuiz(quiz: CreateOrModifyQuizValue): Resource<Unit, NetworkError> {
    return quizService.createQuiz(quiz)
}

suspend fun modifyQuiz(quiz: CreateOrModifyQuizValue): Resource<Unit, NetworkError> {
    return quizService.modifyQuiz(quiz)
}

suspend fun getQuizzesByUserId(id: UUID): Resource<List<Quiz>, NetworkError> {
    return quizService.getQuizzesByUserId(id)
}

suspend fun deleteQuiz(id: UUID): Resource<Unit, NetworkError> {
    return quizService.deleteQuiz(id)
}

suspend fun getFriendsQuizzes(): Resource<List<Quiz>, NetworkError> {
    return quizService.getFriendsQuizzes()
}

suspend fun getRecentlyAddedQuizzes(): Resource<List<Quiz>, NetworkError> {
    return quizService.getRecentlyAddedQuizzes()
}

suspend fun getMostLikedQuizzes(): Resource<List<Quiz>, NetworkError> {
    return quizService.getMostLikedQuizzes()
}
}