package com.github.projektmagma.magmaquiz.presentation.model

sealed interface AuthCommand {
    data class EmailChanged(val email: String) : AuthCommand
    data class PasswordChanged(val password: String) : AuthCommand
    data class RepeatedPasswordChanged(val repeatedPassword: String) : AuthCommand
    data object Login : AuthCommand
    data object Register : AuthCommand
    data object Logout : AuthCommand
}