package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model

sealed interface QuizFilters {
    data object None : QuizFilters
    data object Favorites : QuizFilters
    data object MostLiked : QuizFilters
    data object Friends : QuizFilters
    data object RecentlyAdded : QuizFilters
}