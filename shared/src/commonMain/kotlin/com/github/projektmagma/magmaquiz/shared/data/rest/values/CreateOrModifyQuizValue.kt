package com.github.projektmagma.magmaquiz.shared.data.rest.values

import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizVisibility
import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateOrModifyQuizValue(
    @Serializable(UUIDSerializer::class)
    val id: UUID? = null,
    val quizName: String,
    val quizDescription: String,
    val quizImage: ByteArray? = null,
    val tagList: List<String>,
    val visibility: QuizVisibility,
    val questionList: List<Question>
)