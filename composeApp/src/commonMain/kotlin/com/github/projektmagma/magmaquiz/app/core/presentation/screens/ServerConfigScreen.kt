package com.github.projektmagma.magmaquiz.app.core.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.domain.Protocols
import com.github.projektmagma.magmaquiz.app.core.presentation.ServerConfigViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.LocalEvent
import com.github.projektmagma.magmaquiz.app.core.presentation.model.server.ServerCommand
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerConfigScreen(
    navigateBack: () -> Unit,
    serverConfigViewModel: ServerConfigViewModel = koinViewModel()
) {
    LaunchedEffect(serverConfigViewModel.serverChannel) {
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
    val configs by serverConfigViewModel.configsList.collectAsStateWithLifecycle()
    val name by remember(serverConfig.id) { mutableStateOf(serverConfig.name) }

    var expandedProtocol by remember { mutableStateOf(false) }
    var expandedConfigs by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Dns,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.server_config),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(Res.string.server_connection),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (configs.size > 1) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(Res.string.last_configs),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedConfigs,
                        onExpandedChange = { expandedConfigs = !expandedConfigs }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            value = name,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedConfigs) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedConfigs,
                            onDismissRequest = { expandedConfigs = false }
                        ) {
                            configs.forEach { config ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = config.name,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = "${config.protocol}://${config.ip}${if (config.port.isNotBlank()) ":${config.port}" else ""}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            if (config.id != 1) {
                                                IconButton(
                                                    onClick = {
                                                        serverConfigViewModel.onCommand(
                                                            ServerCommand.DeleteConfig(config)
                                                        )
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Delete",
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    onClick = {
                                        serverConfigViewModel.onCommand(
                                            ServerCommand.ConfigSelected(config)
                                        )
                                        expandedConfigs = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.connection_details),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                HorizontalDivider()

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = serverConfig.name,
                    onValueChange = { serverConfigViewModel.onCommand(ServerCommand.NameChanged(it)) },
                    label = { Text(stringResource(Res.string.name)) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = serverConfig.ip,
                    onValueChange = { serverConfigViewModel.onCommand(ServerCommand.IpChanged(it)) },
                    label = { Text(stringResource(Res.string.ip)) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = serverConfig.port,
                    onValueChange = { serverConfigViewModel.onCommand(ServerCommand.PortChanged(it)) },
                    label = { Text(stringResource(Res.string.port)) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                ExposedDropdownMenuBox(
                    expanded = expandedProtocol,
                    onExpandedChange = { expandedProtocol = !expandedProtocol }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        readOnly = true,
                        value = serverConfig.protocol.name,
                        onValueChange = {},
                        label = { Text(text = stringResource(Res.string.protocol)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProtocol) },
                        shape = MaterialTheme.shapes.medium,
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedProtocol,
                        onDismissRequest = { expandedProtocol = false }
                    ) {
                        Protocols.entries.forEach { protocol ->
                            DropdownMenuItem(
                                text = { Text(text = protocol.name) },
                                onClick = {
                                    serverConfigViewModel.onCommand(
                                        ServerCommand.ProtocolChanged(protocol)
                                    )
                                    expandedProtocol = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { serverConfigViewModel.onCommand(ServerCommand.SettingsSaved) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = stringResource(Res.string.save),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}