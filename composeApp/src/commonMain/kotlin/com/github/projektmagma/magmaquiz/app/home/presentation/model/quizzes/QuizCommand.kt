package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

import com.github.projektmagma.magmaquiz.app.home.presentation.model.QuestionState

sealed interface QuizCommand {
    data class NameChanged(val name: String): QuizCommand
    data class DescriptionChanged(val description: String): QuizCommand
    data class ImageChanged(val byteArray: ByteArray?): QuizCommand
    data class VisibilityChanged(val isPublic: Boolean): QuizCommand
    data class AddNewQuestion(val questionState: QuestionState): QuizCommand
    data object Create: QuizCommand
}