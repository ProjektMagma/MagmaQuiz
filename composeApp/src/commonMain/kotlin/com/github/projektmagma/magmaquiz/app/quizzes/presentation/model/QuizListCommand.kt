package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model

import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import java.util.*

sealed interface QuizListCommand {
    data class NameChanged(val name: String): QuizListCommand
    data class ListChanged(val list: List<Quiz>): QuizListCommand
    data class FilterChanged(val filter: QuizFilters): QuizListCommand
    data class LoadByName(val delay: Boolean = false): QuizListCommand
    data object LoadByFilter: QuizListCommand
    data class FavoriteStatusChanged(val id: UUID): QuizListCommand
}