package com.github.projektmagma.magmaquiz.app.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.data.ServerConfigRepository
import com.github.projektmagma.magmaquiz.app.core.data.database.ServerConfigEntity
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.LocalEvent
import com.github.projektmagma.magmaquiz.app.core.presentation.model.server.ServerCommand
import com.github.projektmagma.magmaquiz.app.core.presentation.model.server.ServerConfigState
import com.github.projektmagma.magmaquiz.app.core.presentation.model.server.toState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServerConfigViewModel(
    private val serverConfigRepository: ServerConfigRepository
): ViewModel() {
    private val _serverConfigState = MutableStateFlow(ServerConfigState())
    val serverConfig = _serverConfigState.asStateFlow()
    
    val configsList = serverConfigRepository.getAllConfigs()
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _serverChannel = Channel<LocalEvent>()
    val serverChannel = _serverChannel.receiveAsFlow()
    
    fun onCommand(serverCommand: ServerCommand){
        when (serverCommand) {
            is ServerCommand.NameChanged -> _serverConfigState.update { currentState -> 
                currentState.copy(name = serverCommand.name, isEdited = true)
            }
            is ServerCommand.IpChanged -> _serverConfigState.update { currentState -> 
                currentState.copy(ip = serverCommand.ip, isEdited = true)
            }
            is ServerCommand.PortChanged -> _serverConfigState.update { currentState ->
                currentState.copy(port = serverCommand.port, isEdited = true)
            }
            is ServerCommand.ProtocolChanged -> _serverConfigState.update { currentState ->
                currentState.copy(protocol = serverCommand.protocol, isEdited = true)
            }
            is ServerCommand.ConfigSelected -> {
                _serverConfigState.value = serverCommand.config.toState()
                update()
            }
            is ServerCommand.DeleteConfig -> deleteConfig(serverCommand.config)
            ServerCommand.SettingsSaved -> {
                if (_serverConfigState.value.isEdited) {
                    insert()
                }
                _serverChannel.trySend(LocalEvent.Success)
            }
        }
    }
    
    init {
        viewModelScope.launch {
            _serverConfigState.value = serverConfigRepository.getSelectedConfig().toState()
        }
    }
    
    private fun insert(){
        viewModelScope.launch {
            serverConfigRepository.insert(_serverConfigState.value)
        }
    }
    
    private fun update(){
        viewModelScope.launch { 
            serverConfigRepository.update(_serverConfigState.value)
        }
    }
    
    private fun deleteConfig(serverConfigEntity: ServerConfigEntity){
        viewModelScope.launch { 
            serverConfigRepository.deleteConfig(serverConfigEntity)
        }
    }
}