package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model

import java.util.UUID

sealed interface QuizListCommand {
    data class NameChanged(val name: String): QuizListCommand
    data class FilterChanged(val filter: QuizFilters): QuizListCommand
    data class LoadByName(val delay: Boolean = false): QuizListCommand
    data object LoadMore: QuizListCommand
    data class FavoriteStatusChanged(val id: UUID): QuizListCommand
}