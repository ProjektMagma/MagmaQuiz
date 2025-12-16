package com.github.projektmagma.magmaquiz.presentation.model

import com.github.projektmagma.magmaquiz.domain.NetworkError

sealed interface AuthEvent {
    data object Success : AuthEvent
    data class Failure(val networkError: NetworkError) : AuthEvent
}