package com.github.projektmagma.magmaquiz.app.auth.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.UserRepository
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validateEmail
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validateIsEmptyPassword
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validatePassword
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validateUsername
import com.github.projektmagma.magmaquiz.app.auth.presentation.model.auth.AuthCommand
import com.github.projektmagma.magmaquiz.app.auth.presentation.model.auth.AuthEvent
import com.github.projektmagma.magmaquiz.app.auth.presentation.model.auth.AuthState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    val thisUser = repository.thisUser
    var state by mutableStateOf(AuthState())

    private val _authChannel = Channel<AuthEvent>()
    val authChannel = _authChannel.receiveAsFlow()


    fun onCommand(command: AuthCommand) {
        when (command) {
            is AuthCommand.UsernameChanged -> state = state.copy(username = command.username, usernameError = null)
            is AuthCommand.EmailChanged -> state = state.copy(email = command.email, emailError = null)
            is AuthCommand.PasswordChanged -> state = state.copy(password = command.password, passwordError = null)
            AuthCommand.Login -> loginUser(state.email, state.password)
            AuthCommand.Register -> registerUser(state.username, state.email, state.password)
            AuthCommand.Logout -> logoutUser()
            AuthCommand.WhoAmI -> whoAmI()
        }
    }

    private fun loginUser(email: String, password: String) {
        state = state.copy(
            emailError = validateEmail(email),
            passwordError = validateIsEmptyPassword(password)
        )

        if (listOf(
                state.emailError,
                state.passwordError
            ).any { it != null }
        ) return

        viewModelScope.launch {
            when (val result = repository.loginUser(
                email = email,
                password = password
            )) {
                is Resource.Error -> {
                    _authChannel.trySend(AuthEvent.Failure(result.error))
                }

                is Resource.Success -> {
                    _authChannel.trySend(AuthEvent.Success)
                }
            }
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        state = state.copy(
            usernameError = validateUsername(username),
            emailError = validateEmail(email),
            passwordError = validatePassword(password),
        )

        if (listOf(
                state.usernameError,
                state.emailError,
                state.passwordError,
            ).any { it != null }
        ) return

        viewModelScope.launch {
            when (val result = repository.registerUser(
                username = username,
                email = email,
                password = password
            )) {
                is Resource.Error -> {
                    _authChannel.trySend(AuthEvent.Failure(result.error))
                }

                is Resource.Success -> {
                    _authChannel.trySend(AuthEvent.Success)
                }
            }
        }
    }

    private fun logoutUser() {
        viewModelScope.launch {
            when (val result = repository.logoutUser()) {
                is Resource.Error -> {
                    _authChannel.trySend(AuthEvent.Failure(result.error))
                }

                is Resource.Success -> {
                    _authChannel.trySend(AuthEvent.Success)
                }
            }
        }
    }

    private fun whoAmI() {
        viewModelScope.launch {
            when (val result = repository.whoAmI()) {
                is Resource.Error -> {
                    _authChannel.trySend(AuthEvent.Failure(result.error))
                }

                is Resource.Success -> {
                    _authChannel.trySend(AuthEvent.Success)
                }
            }
        }
    }
}