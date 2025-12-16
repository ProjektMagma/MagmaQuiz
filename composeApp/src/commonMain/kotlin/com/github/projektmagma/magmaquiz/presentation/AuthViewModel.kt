package com.github.projektmagma.magmaquiz.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.projektmagma.magmaquiz.data.UserRepository
import com.github.projektmagma.magmaquiz.presentation.model.AuthCommand
import com.github.projektmagma.magmaquiz.presentation.model.AuthEvent
import com.github.projektmagma.magmaquiz.presentation.model.AuthState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {
    var state by mutableStateOf(AuthState())
    
    private val _authChannel = Channel<AuthEvent>()
    val authChannel = _authChannel.receiveAsFlow()
    
    fun onCommand(command: AuthCommand){
        when (command){
            is AuthCommand.EmailChanged -> state = state.copy(email = command.email)
            is AuthCommand.PasswordChanged -> state = state.copy(password = command.password)
            is AuthCommand.RepeatedPasswordChanged -> state = state.copy(repeatedPassword = command.repeatedPassword)
            AuthCommand.Login -> loginUser()
            AuthCommand.Register -> registerUser()
        }
    }

    private fun loginUser() {
        TODO("Not yet implemented")
    }

    private fun registerUser() {
        TODO("Not yet implemented")
    }
}