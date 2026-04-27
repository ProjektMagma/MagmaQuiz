package com.github.projektmagma.magmaquiz.app.settings.presentation.screens.email.change

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validateEmail
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.settings.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.email.change.EmailChangeCommand
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.email.change.EmailChangeState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmailChangeViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(EmailChangeState())
    val state = _state.asStateFlow()
    
    val user = authRepository.thisUser.asStateFlow()
    
    private val _event = Channel<NetworkEvent>()
    val event = _event.receiveAsFlow()
    
    fun onCommand(command: EmailChangeCommand) {
        when (command) {
            is EmailChangeCommand.ChangeEmail -> _state.update { it.copy(email = command.newEmail, emailError = null) }
            EmailChangeCommand.CheckIsEmailTaken -> checkIsEmailTaken()
        }
    }
    
    private fun checkIsEmailTaken(){
        _state.update { it.copy(emailError = validateEmail(_state.value.email)) }
        if (_state.value.emailError != null) return
        
        viewModelScope.launch { 
            settingsRepository.checkIsEmailTaken(_state.value.email)
                .whenSuccess { 
                    _event.trySend(NetworkEvent.Success)
                }
                .whenError { result ->
                    when (result.error){
                        NetworkError.CONFLICT -> _state.update { it.copy(emailError = result.error) }
                        else -> { _event.trySend(NetworkEvent.Failure(result.error)) }
                    }
                }
        }
    }
}