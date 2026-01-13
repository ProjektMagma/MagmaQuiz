package com.github.projektmagma.magmaquiz.server.data.conversion

sealed interface QuizConversionCommand : ConversionCommand {
    data object WithUserAndQuestions : QuizConversionCommand
    data object WithUserNoQuestions : QuizConversionCommand
    data object WithoutUserAndQuestions : QuizConversionCommand
}