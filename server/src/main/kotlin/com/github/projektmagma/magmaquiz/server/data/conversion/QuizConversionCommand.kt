package com.github.projektmagma.magmaquiz.server.data.conversion

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity

sealed interface QuizConversionCommand : ConversionCommand {
    data class WithUserAndQuestions(val caller: UserEntity) : QuizConversionCommand
    data class WithUserNoQuestions(val caller: UserEntity) : QuizConversionCommand
    data class WithoutUserAndQuestions(val caller: UserEntity) : QuizConversionCommand
}