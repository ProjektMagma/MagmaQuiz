package com.github.projektmagma.magmaquiz.app.settings.presentation.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
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
            is AccountDetailsChangeCommand.UsernameChanged -> _state.update { it.copy(username = command.newUsername, usernameError = null) }
            AccountDetailsChangeCommand.Save -> { save() }
        }
    }
    
    init {
        _state.update { 
            it.copy(
                username = user?.userName!!,
                bio = user.userBio
            )
        }
    }
    
    private fun save(){
        viewModelScope.launch {
            val username = _state.value.username
            val bio = _state.value.bio
            
            _state.update { 
                it.copy(usernameError = validateUsername(username))
            }
            
            if (_state.value.usernameError != null) {return@launch}

            val success = buildList {
                if (user?.userName != username) add(settingsRepository.changeUsername(username).whenError { result ->
                    if (result.error == NetworkError.CONFLICT) _state.update { it.copy(usernameError = result.error) }
                })
                add(settingsRepository.changeBio(bio))
            }.all { it is Resource.Success }

            if (success) {
                authRepository.thisUser.update {
                    it?.copy(
                        userName = username,
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