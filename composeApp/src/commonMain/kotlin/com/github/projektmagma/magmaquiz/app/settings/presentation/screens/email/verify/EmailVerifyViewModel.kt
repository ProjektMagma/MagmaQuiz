package com.github.projektmagma.magmaquiz.app.settings.presentation.screens.email.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.Timer
import com.github.projektmagma.magmaquiz.app.settings.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.email.verify.EmailVerifyCommand
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.email.verify.EmailVerifyState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmailVerifyViewModel(
    private val email: String,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _event = Channel<NetworkEvent>()
    val event = _event.receiveAsFlow()
    
    private val _state = MutableStateFlow(EmailVerifyState())
    val state = _state.asStateFlow()
    
    private val resendTimer = Timer(viewModelScope)
    
    init {
        sendVerificationEmail()
        startResendTimer(15)
    }
    
    fun onCommand(command: EmailVerifyCommand) {
        when (command) {
            is EmailVerifyCommand.ChangeVerificationCode -> _state.update { it.copy(code = command.newCode) }
            EmailVerifyCommand.ResendCode -> {
                startResendTimer(30)
                sendVerificationEmail()
            }
            EmailVerifyCommand.VerifyCode -> verifyCode()
            EmailVerifyCommand.SendVerificationEmail -> sendVerificationEmail()
        }   
    }

    private fun verifyCode() {
        viewModelScope.launch {
            settingsRepository.confirmChangeEmail(email, _state.value.code)
                .whenSuccess {
                    authRepository.thisUser.update { it?.copy(userEmail = email) }
                    _event.trySend(NetworkEvent.Success)
                }
                .whenError {
                    _event.trySend(NetworkEvent.Failure(it.error))
                }
        }
    }

    private fun sendVerificationEmail(){
        viewModelScope.launch {
            settingsRepository.sendVerificationCode(email)
        }
    }

    private fun startResendTimer(from: Int) {
        resendTimer.start(
            from = from,
            onTick = { left ->
                _state.update { it.copy(timeToResend = left) }
            }
        )
    }
    
}