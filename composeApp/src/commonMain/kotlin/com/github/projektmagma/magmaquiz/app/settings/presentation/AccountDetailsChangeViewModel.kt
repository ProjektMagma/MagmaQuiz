package com.github.projektmagma.magmaquiz.app.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validateEmail
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validateUsername
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.LocalEvent
import com.github.projektmagma.magmaquiz.app.settings.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.account.AccountDetailsChangeCommand
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.account.AccountDetailsChangeState
import com.github.projektmagma.magmaquiz.app.users.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountDetailsChangeViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository
): ViewModel() {
    private val _state = MutableStateFlow(AccountDetailsChangeState())
    val state = _state.asStateFlow()
    
    private val _event = Channel<LocalEvent>()
    val event = _event.receiveAsFlow()
    
    val user = authRepository.thisUser.value
    
    fun onCommand(command: AccountDetailsChangeCommand){
        when (command) {
            is AccountDetailsChangeCommand.BioChanged -> _state.update { it.copy(bio = command.newBio) }
            is AccountDetailsChangeCommand.EmailChanged -> _state.update { it.copy(email = command.newEmail, emailError = null) }
            is AccountDetailsChangeCommand.UsernameChanged -> _state.update { it.copy(username = command.newUsername, usernameError = null) }
            AccountDetailsChangeCommand.Save -> { save() }
        }
    }
    
    init {
        _state.update { 
            it.copy(
                username = user?.userName!!,
                email = user.userEmail,
                bio = user.userBio
            )
        }
    }
    
    private fun save(){
        viewModelScope.launch {
            val email = _state.value.email
            val username = _state.value.username
            val bio = _state.value.bio
            
            _state.update { 
                it.copy(
                    emailError = validateEmail(email),
                    usernameError = validateUsername(username)
                )
            }
            
            val ifError = listOf(
                _state.value.emailError,
                _state.value.usernameError
            ).any { it != null }
            
            if (ifError) {
                return@launch 
            }

            val success = buildList {
                if (user?.userEmail != email) add(settingsRepository.changeEmail(email).whenError { result ->
                    if (result.error == NetworkError.CONFLICT) _state.update { it.copy(emailError = result.error) }
                })
                if (user?.userName != username) add(settingsRepository.changeUsername(username).whenError { result ->
                    if (result.error == NetworkError.CONFLICT) _state.update { it.copy(usernameError = result.error) }
                })
                add(settingsRepository.changeBio(bio))
            }.all { it is Resource.Success }

            if (success) {
                authRepository.thisUser.update {
                    it?.copy(
                        userName = username,
                        userEmail = email,
                        userBio = bio
                    )
                }
                usersRepository.user.value = authRepository.thisUser.value
                _event.send(LocalEvent.Success)
            } else {
                _event.send(LocalEvent.Failure)
            }
        }
    }
}