package com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.core.util.Timer
import com.github.projektmagma.magmaquiz.app.settings.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.verify.PasswordVerifyCommand
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.verify.PasswordVerifyEvent
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.verify.PasswordVerifyState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PasswordVerifyViewModel(
    private val settingsRepository: SettingsRepository,
    authRepository: AuthRepository
) : ViewModel() {
    private val _code = MutableStateFlow("")
    private val _remainingSeconds = MutableStateFlow(15)
    private val _codeError = MutableStateFlow<NetworkError?>(null)
    private val _event = Channel<PasswordVerifyEvent>()
    val event = _event.receiveAsFlow()
    
    private val _email = settingsRepository.passwordEmailEntryState.value.email ?: authRepository.thisUser.value?.userEmail ?: ""

    val state = combine(
        _code,
        settingsRepository.passwordEmailEntryState,
        _remainingSeconds,
        _codeError
    ) { code, passwordEntryEmailState, remainingSeconds, codeError ->
        PasswordVerifyState(
            code = code,
            email = passwordEntryEmailState.email ?: authRepository.thisUser.value?.userEmail ?: "",
            remainingSeconds = remainingSeconds,
            codeError = codeError
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        PasswordVerifyState()
    )
    
    private val timer = Timer(
        scope = viewModelScope
    )
    
    init {
        startTimer()
        sendCode()
    }
    
    fun onCommand(command: PasswordVerifyCommand){
        when (command){
            is PasswordVerifyCommand.CodeChanged -> {
                _code.value = command.newCode
                _codeError.value = null
            }
            PasswordVerifyCommand.SendCode -> {
                startTimer()
                sendCode()
            }
            PasswordVerifyCommand.Verify -> verify()
        }
    }
    
    private fun sendCode(){
        viewModelScope.launch {
            settingsRepository.sendVerificationCode(_email)
                .whenSuccess { _event.send(PasswordVerifyEvent.CodeSent) }
                .whenError { _event.send(PasswordVerifyEvent.FailureSent) }
        }
    }
    
    private fun verify(){
        viewModelScope.launch { 
            settingsRepository.verifyPasswordCode(_email, state.value.code)
                .whenError { 
                    val networkError = it.error
                    if (networkError == NetworkError.FORBIDDEN) {
                        _codeError.value = networkError
                    } else {
                        _event.send(PasswordVerifyEvent.FailureVerify)
                    }
                }
                .whenSuccess { _event.send(PasswordVerifyEvent.Verified) }
        }
    }
    
    private fun startTimer(){
        timer.start(
            from = 15,
            onTick = { _remainingSeconds.value = it }
        )
    }
}