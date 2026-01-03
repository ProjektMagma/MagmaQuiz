package com.github.projektmagma.magmaquiz.shared.data.rest.values

import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrModifyQuizValue(
    val quiz: Quiz
)
