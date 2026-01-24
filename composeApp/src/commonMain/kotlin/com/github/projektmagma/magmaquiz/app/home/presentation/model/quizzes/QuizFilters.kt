package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

sealed interface QuizFilters {
    data object None : QuizFilters
    data object Favorites : QuizFilters
    data object MostLiked : QuizFilters
    data object Friends : QuizFilters
    data object RecentlyAdded : QuizFilters
}