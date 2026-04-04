package com.github.projektmagma.magmaquiz.app.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validateEmail
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validateIsEmptyPassword
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validatePassword
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validateUsername
import com.github.projektmagma.magmaquiz.app.auth.presentation.model.auth.AuthCommand
import com.github.projektmagma.magmaquiz.app.auth.presentation.model.auth.AuthState
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    val thisUser = repository.thisUser
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _authChannel = Channel<NetworkEvent>()
    val authChannel = _authChannel.receiveAsFlow()


    fun onCommand(command: AuthCommand) {
        when (command) {
            is AuthCommand.UsernameChanged -> _state.update { it.copy(username = command.username, usernameError = null) } 
            is AuthCommand.EmailChanged -> _state.update { it.copy(email = command.email, emailError = null) }
            is AuthCommand.PasswordChanged -> _state.update { it.copy(password = command.password, passwordError = null) }
            AuthCommand.Login -> loginUser()
            AuthCommand.Register -> registerUser()
            AuthCommand.Logout -> logoutUser()
            AuthCommand.WhoAmI -> whoAmI()
        }
    }

    private fun loginUser() {
        _state.update {
            it.copy(
                emailError = validateEmail(_state.value.email),
                passwordError = validateIsEmptyPassword(_state.value.password)
            )
        }

        if (listOf(
                _state.value.emailError,
                _state.value.passwordError
            ).any { it != null }
        ) return

        viewModelScope.launch {
            when (val result = repository.loginUser(
                email = _state.value.email,
                password = _state.value.password
            )) {
                is Resource.Error -> {
                    _authChannel.trySend(NetworkEvent.Failure(result.error))
                }

                is Resource.Success -> {
                    _authChannel.trySend(NetworkEvent.Success)
                }
            }
        }
    }

    private fun registerUser() {
        _state.update {
            it.copy(
                usernameError = validateUsername(_state.value.username.trim()),
                emailError = validateEmail(_state.value.email.trim()),
                passwordError = validatePassword(_state.value.password.trim()),
            )
        }
        
        if (listOf(
                _state.value.usernameError,
                _state.value.emailError,
                _state.value.passwordError,
            ).any { it != null }
        ) return

        viewModelScope.launch {
            when (val result = repository.registerUser(
                username = _state.value.username,
                email = _state.value.email,
                password = _state.value.password
            )) {
                is Resource.Error -> {
                    when (result.error) {
                        NetworkError.CONFLICT -> _state.update { it.copy(emailError = result.error) }
                        else -> { _authChannel.trySend(NetworkEvent.Failure(result.error)) }
                    }
                }

                is Resource.Success -> {
                    _authChannel.trySend(NetworkEvent.Success)
                }
            }
        }
    }

    private fun logoutUser() {
        viewModelScope.launch {
            when (val result = repository.logoutUser()) {
                is Resource.Error -> {
                    _authChannel.trySend(NetworkEvent.Failure(result.error))
                }

                is Resource.Success -> {
                    _authChannel.trySend(NetworkEvent.Success)
                }
            }
        }
    }

    private fun whoAmI() {
        viewModelScope.launch {
            when (val result = repository.whoAmI()) {
                is Resource.Error -> {
                    _authChannel.trySend(NetworkEvent.Failure(result.error))
                }

                is Resource.Success -> {
                    _authChannel.trySend(NetworkEvent.Success)
                }
            }
        }
    }
}