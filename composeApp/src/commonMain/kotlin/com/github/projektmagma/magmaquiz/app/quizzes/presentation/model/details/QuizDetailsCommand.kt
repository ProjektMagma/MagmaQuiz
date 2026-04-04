package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.details

sealed interface QuizDetailsCommand {
    data object GetQuizById : QuizDetailsCommand
    data object ChangeFavoriteStatus : QuizDetailsCommand
    data object AddQuizToHistory : QuizDetailsCommand
    data object SetupQuizForGame : QuizDetailsCommand
}