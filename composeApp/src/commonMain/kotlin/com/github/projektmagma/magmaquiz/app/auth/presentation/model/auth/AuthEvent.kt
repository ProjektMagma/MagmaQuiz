package com.github.projektmagma.magmaquiz.app.auth.presentation.model.auth

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError

sealed interface AuthEvent {
    data object Success : AuthEvent
    data class Failure(val networkError: NetworkError) : AuthEvent
}