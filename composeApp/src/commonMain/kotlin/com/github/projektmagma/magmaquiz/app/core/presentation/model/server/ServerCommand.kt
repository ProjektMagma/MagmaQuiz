package com.github.projektmagma.magmaquiz.app.core.presentation.model.server

import com.github.projektmagma.magmaquiz.app.core.data.database.ServerConfigEntity
import com.github.projektmagma.magmaquiz.app.core.domain.Protocols


sealed interface ServerCommand {
    data class NameChanged(val name: String) : ServerCommand
    data class IpChanged(val ip: String) : ServerCommand
    data class PortChanged(val port: String) : ServerCommand
    data class ProtocolChanged(val protocol: Protocols) : ServerCommand
    data class ConfigSelected(val config: ServerConfigEntity) : ServerCommand
    data class DeleteConfig(val config: ServerConfigEntity): ServerCommand
    data object SettingsSaved : ServerCommand
}