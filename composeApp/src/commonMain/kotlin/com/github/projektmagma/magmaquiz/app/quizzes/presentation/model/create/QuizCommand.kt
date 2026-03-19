package com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create

import com.github.projektmagma.magmaquiz.shared.data.domain.QuizVisibility
import java.util.UUID

sealed interface QuizCommand {
    sealed interface QuizProperties : QuizCommand {
        data class NameChanged(val name: String) : QuizProperties
        data class DescriptionChanged(val description: String) : QuizProperties
        data class ImageChanged(val image: ByteArray?) : QuizProperties
        data class VisibilityChanged(val visibility: QuizVisibility) : QuizProperties
    }
    
    sealed interface QuestionEditor : QuizCommand {
        data class Init(val isMultiple: Boolean) : QuestionEditor
        data class SetForEditing(val questionModel: QuestionModel) : QuestionEditor
        data class ContentChanged(val content: String) : QuestionEditor
        data class ImageChanged(val byteArray: ByteArray?) : QuestionEditor
        data class AnswerContentChanged(val content: String, val index: Int) : QuestionEditor
        data class AnswerCorrectnessChanged(val isCorrect: Boolean, val index: Int) : QuestionEditor
        data class RemoveAnswer(val index: Int): QuestionEditor
        data object AddAnswer: QuestionEditor
        data class SaveQuestion(val questionModel: QuestionModel) : QuestionEditor
    }
    data class SetForEdit(val id: UUID) : QuizCommand
    data class TagNameChanged(val name: String) : QuizCommand
    data class AddNewTag(val tagName: String) : QuizCommand
    data object GetTags : QuizCommand
    data class RemoveTag(val name: String) : QuizCommand
    data object CreateQuiz : QuizCommand
    data object ResetState : QuizCommand
}