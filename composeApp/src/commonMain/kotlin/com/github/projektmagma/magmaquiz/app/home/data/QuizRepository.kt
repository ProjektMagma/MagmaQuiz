package com.github.projektmagma.magmaquiz.app.home.data

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import java.util.UUID

class QuizRepository(
    private val quizService: QuizService
) {
    suspend fun getQuizByName(name: String): Resource<List<Quiz>, NetworkError> {
        return quizService.getQuizByName(name)
    }
    
    suspend fun getQuizById(id: UUID): Resource<Quiz, NetworkError> {
        return quizService.getQuizById(id)
    }
}