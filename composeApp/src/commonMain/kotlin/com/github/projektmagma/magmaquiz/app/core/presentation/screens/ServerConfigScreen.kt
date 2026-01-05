package com.github.projektmagma.magmaquiz.app.core.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.ServerConfigViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.LocalEvent
import com.github.projektmagma.magmaquiz.app.core.presentation.model.server.ServerCommand
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerConfigScreen(
    navigateBack: () -> Unit,
    serverConfigViewModel: ServerConfigViewModel = koinViewModel()
){
    LaunchedEffect(serverConfigViewModel.serverChannel){
        serverConfigViewModel.serverChannel.collect { event ->
            when (event) {
                LocalEvent.Failure -> SnackbarController.onEvent("Nie udalo sie zapisac")
                LocalEvent.Success -> {
                    navigateBack()
                    SnackbarController.onEvent("Pomyslnie zapisano ustawienia")
                }
            }
        }
    }
    
    val serverConfig by serverConfigViewModel.serverConfig.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = serverConfig.ip,
            onValueChange = { serverConfigViewModel.onCommand(ServerCommand.IpChanged(it)) }
        )
        OutlinedTextField(
            value = serverConfig.port,
            onValueChange = { serverConfigViewModel.onCommand(ServerCommand.PortChanged(it)) }
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded}
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                readOnly = true, 
                value = serverConfig.protocol.name,
                onValueChange = {},
                label = { Text("Protokół") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = serverConfig.protocol.name) },
                    onClick = {
                        serverConfigViewModel.onCommand(ServerCommand.ProtocolChanged(serverConfig.protocol))
                        expanded = false
                    }
                )
                val otherProtocol = Protocols.entries.first { it != serverConfig.protocol }
                DropdownMenuItem(
                    text = { Text(text = otherProtocol.name) },
                    onClick = {
                        serverConfigViewModel.onCommand(ServerCommand.ProtocolChanged(otherProtocol))
                        expanded = false
                    }
                )
            }
        }

        Button(
            onClick = {
                serverConfigViewModel.onCommand(ServerCommand.SettingsSaved)
            }
        ) {
            Text("Zapisz")
        }
    }
}

enum class Protocols {
    HTTP,
    HTTPS
}