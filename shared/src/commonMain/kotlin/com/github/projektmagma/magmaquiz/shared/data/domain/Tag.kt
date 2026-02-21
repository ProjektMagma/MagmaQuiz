package com.github.projektmagma.magmaquiz.shared.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val tagName: String,
    val quizzesCount: Long
)
