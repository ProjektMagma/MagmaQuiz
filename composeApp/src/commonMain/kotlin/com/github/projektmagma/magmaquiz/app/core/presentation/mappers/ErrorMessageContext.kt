package com.github.projektmagma.magmaquiz.app.core.presentation.mappers

sealed interface ErrorMessageContext {
    data object Auth : ErrorMessageContext
    data object Server : ErrorMessageContext
}