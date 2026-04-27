package com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.entry

import androidx.lifecycle.ViewModel
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validateEmail
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.LocalEvent
import com.github.projektmagma.magmaquiz.app.settings.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.entry.PasswordEmailEntryCommand
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class PasswordEmailEntryViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _state = settingsRepository.passwordEmailEntryState
    val state = _state.asStateFlow()
    
    private val _event = Channel<LocalEvent>()
    val event = _event.receiveAsFlow()
    
    fun onCommand(command: PasswordEmailEntryCommand){
        when (command){
            is PasswordEmailEntryCommand.EmailChanged -> _state.update { it.copy(email = command.newEmail) }
            PasswordEmailEntryCommand.CheckEmail -> {
                _state.update { it.copy(emailError = validateEmail(_state.value.email ?: "")) }
                if (_state.value.emailError == null){
                    _event.trySend(LocalEvent.Success)
                }
            }
        }
    }
}