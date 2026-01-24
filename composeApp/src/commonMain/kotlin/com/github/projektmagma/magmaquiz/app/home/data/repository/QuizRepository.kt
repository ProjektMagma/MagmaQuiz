package com.github.projektmagma.magmaquiz.app.home.data.repository

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.home.data.service.QuizService
import com.github.projektmagma.magmaquiz.app.home.presentation.model.game.GameState
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class QuizRepository(
    private val quizService: QuizService
) {
    val quiz = MutableStateFlow<Quiz?>(null)

    var gameState = MutableStateFlow(GameState())

    suspend fun getQuizByName(name: String): Resource<List<Quiz>, NetworkError> {
        return quizService.getQuizByName(name)
    }

    suspend fun getQuizById(id: UUID): Resource<Quiz, NetworkError> {
        return quizService.getQuizById(id)
    }

    suspend fun changeFavoriteStatus(id: UUID): Resource<Unit, NetworkError> {
        return quizService.changeFavoriteStatus(id)
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
    
    suspend fun deleteQuiz(id: UUID): Resource<Unit, NetworkError>{
        return quizService.deleteQuiz(id)
    }
}