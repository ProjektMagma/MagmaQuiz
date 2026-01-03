package com.github.projektmagma.magmaquiz.app.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.data.ServerConfigDataStore
import com.github.projektmagma.magmaquiz.app.core.presentation.model.server.ServerCommand
import com.github.projektmagma.magmaquiz.app.core.presentation.model.server.ServerConfig
import com.github.projektmagma.magmaquiz.app.core.presentation.model.server.ServerEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServerConfigViewModel(
    private val serverConfigDataStore: ServerConfigDataStore
): ViewModel() {
    private val _serverConfig = MutableStateFlow(ServerConfig())
    val serverConfig = _serverConfig.asStateFlow()
    
    private val _serverChannel = Channel<ServerEvent>()
    val serverChannel = _serverChannel.receiveAsFlow()
    
    fun onCommand(serverCommand: ServerCommand){
        when (serverCommand) {
            is ServerCommand.IpChanged -> _serverConfig.update { currentState -> 
                currentState.copy(ip = serverCommand.ip)
            }
            is ServerCommand.PortChanged -> _serverConfig.update { currentState ->
                currentState.copy(port = serverCommand.port)
            }
            is ServerCommand.ProtocolChanged -> _serverConfig.update { currentState ->
                currentState.copy(protocol = serverCommand.protocol)
            }
            ServerCommand.SettingsSaved -> setServerConfiguration()
        }
    }
    
    init {
        viewModelScope.launch {
            _serverConfig.value = serverConfigDataStore.getServerConfig()
        }
    }
    
    private fun setServerConfiguration(){
        viewModelScope.launch {
            val result = serverConfigDataStore.setServerConfiguration(_serverConfig.value)
            when (result) {
                true -> _serverChannel.send(ServerEvent.Success)
                false -> _serverChannel.send(ServerEvent.Failure)
            }
        }
    }
}