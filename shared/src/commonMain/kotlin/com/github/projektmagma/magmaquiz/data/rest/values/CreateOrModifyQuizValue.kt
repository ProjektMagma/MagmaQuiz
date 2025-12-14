package com.github.projektmagma.magmaquiz.data.rest.values

import com.github.projektmagma.magmaquiz.data.domain.Quiz
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrModifyQuizValue(
    val quiz: Quiz
)
