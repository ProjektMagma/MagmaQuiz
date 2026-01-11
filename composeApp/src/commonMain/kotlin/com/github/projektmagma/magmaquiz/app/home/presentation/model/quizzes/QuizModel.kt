package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

import io.github.vinceglb.filekit.PlatformFile

data class QuizModel(
    val name: String = "",
    val description: String = "",
    val image: PlatformFile? = null,
    val isPublic: Boolean = false,
    val questionList: List<QuestionModel> = emptyList()
)