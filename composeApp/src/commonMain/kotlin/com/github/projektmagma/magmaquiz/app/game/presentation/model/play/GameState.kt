package com.github.projektmagma.magmaquiz.app.game.presentation.model.play

data class GameState(
    val score: Int = 0,
    val currentQuestionIndex: Int = 0,
    val questionContent: String = "",
    val questionNumber: Int = 0,
    val questionImage: ByteArray? = null,
    val answers: List<AnswerState> = emptyList(),
    val isAnswered: Boolean = false,
    val isQuizFinished: Boolean = false,
    val totalQuestions: Int = 0,
    val remainingSeconds: Int = 0
)