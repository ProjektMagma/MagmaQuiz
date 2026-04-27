package com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.change

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validateIsEmptyPassword
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.validatePassword
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.settings.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.change.PasswordChangeCommand
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.change.PasswordChangeState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PasswordChangeViewModel(
    private val forgot: Boolean,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PasswordChangeState())
    val state = _state.asStateFlow()
    
    private val _event = Channel<NetworkEvent>()
    val event = _event.receiveAsFlow()

    fun onCommand(command: PasswordChangeCommand) {
        when (command) {
            is PasswordChangeCommand.NewPasswordChanged -> _state.update { it.copy(newPassword = command.password, newPasswordError = null) }
            is PasswordChangeCommand.OldPasswordChanged -> _state.update { it.copy(oldPassword = command.password, oldPasswordError = null) }
            is PasswordChangeCommand.RepeatedPasswordChange -> _state.update { it.copy(repeatedPassword = command.password, passwordMatch = null) }
            PasswordChangeCommand.Save -> save()
        }
    }

    private fun save() {
        _state.update { 
            it.copy(
                oldPasswordError = if (forgot) null else validateIsEmptyPassword(_state.value.oldPassword),
                newPasswordError = validatePassword(_state.value.newPassword),
                passwordMatch = _state.value.newPassword == _state.value.repeatedPassword
            )
        }

        val hasErrors =
            _state.value.oldPasswordError != null||
                    _state.value.newPasswordError != null ||
                    _state.value.passwordMatch == false

        if (hasErrors) return
        
        val email = authRepository.thisUser.value?.userEmail ?: settingsRepository.passwordEmailEntryState.value.email ?: "" 
        
        viewModelScope.launch { 
            if (forgot) {
                settingsRepository.changePassword( email, _state.value.newPassword)
            } else {
                settingsRepository.changePasswordWithOld(_state.value.oldPassword, _state.value.newPassword)
            }.whenSuccess { 
                _event.send(NetworkEvent.Success)
            }.whenError { 
                _event.send(NetworkEvent.Failure(it.error))
            }
        }
    }
}