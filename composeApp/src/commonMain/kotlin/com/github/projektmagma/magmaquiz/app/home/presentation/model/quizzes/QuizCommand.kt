package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

import java.util.UUID

sealed interface QuizCommand {
    data class NameChanged(val name: String): QuizCommand
    data class DescriptionChanged(val description: String): QuizCommand
    data class ImageChanged(val byteArray: ByteArray): QuizCommand
    data class VisibilityChanged(val isPublic: Boolean): QuizCommand
    data class QuestionContentChanged(val content: String, val questionId: UUID): QuizCommand
    data class AnswerContentChanged(val content: String, val questionId: UUID, val answerId: UUID): QuizCommand
    data class AddNewAnswer(val questionId: UUID): QuizCommand
    data object AddNewQuestion: QuizCommand
    data object Create: QuizCommand
}