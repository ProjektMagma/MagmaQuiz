package com.github.projektmagma.magmaquiz.presentation.model.auth

import com.github.projektmagma.magmaquiz.domain.NetworkError

sealed interface AuthEvent {
    data object Success : AuthEvent
    data class Failure(val networkError: NetworkError) : AuthEvent
}