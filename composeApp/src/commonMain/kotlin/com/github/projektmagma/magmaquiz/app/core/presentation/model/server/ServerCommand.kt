package com.github.projektmagma.magmaquiz.app.core.presentation.model.server

import com.github.projektmagma.magmaquiz.app.core.presentation.screens.Protocols


sealed interface ServerCommand {
    data class IpChanged(val ip: String) : ServerCommand
    data class PortChanged(val port: String) : ServerCommand
    data class ProtocolChanged(val protocol: Protocols) : ServerCommand
    data object SettingsSaved : ServerCommand
}