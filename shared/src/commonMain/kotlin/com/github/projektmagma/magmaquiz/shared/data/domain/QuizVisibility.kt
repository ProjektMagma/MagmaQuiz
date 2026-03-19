package com.github.projektmagma.magmaquiz.shared.data.domain

import kotlinx.serialization.Serializable

@Serializable
enum class QuizVisibility(val numberInDatabase: Int) {
    Private(0),
    FriendsOnly(1),
    Public(2)
}