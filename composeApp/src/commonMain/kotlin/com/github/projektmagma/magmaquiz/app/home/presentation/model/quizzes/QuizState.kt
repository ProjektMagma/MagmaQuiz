package com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes

import com.github.projektmagma.magmaquiz.app.home.presentation.model.QuestionState

data class QuizState(
    val name: String = "",
    val description: String = "",
    val image: ByteArray? = null,
    val isPublic: Boolean = false,
    val questionList: List<QuestionState> = emptyList()
)