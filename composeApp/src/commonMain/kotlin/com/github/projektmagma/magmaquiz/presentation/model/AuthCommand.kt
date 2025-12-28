package com.github.projektmagma.magmaquiz.presentation.model

sealed interface AuthCommand {
    data class UsernameChanged(val username: String) : AuthCommand
    data class EmailChanged(val email: String) : AuthCommand
    data class PasswordChanged(val password: String) : AuthCommand
    data object Login : AuthCommand
    data object Register : AuthCommand
    data object Logout : AuthCommand
    data object WhoAmI : AuthCommand
}

