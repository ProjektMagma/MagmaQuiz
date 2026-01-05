package com.github.projektmagma.magmaquiz.app.core.presentation.model.events

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError

sealed interface NetworkEvent {
    data object Success : NetworkEvent
    data class Failure(val networkError: NetworkError) : NetworkEvent
}