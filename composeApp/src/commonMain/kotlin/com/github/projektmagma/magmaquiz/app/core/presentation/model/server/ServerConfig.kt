package com.github.projektmagma.magmaquiz.app.core.presentation.model.server

import com.github.projektmagma.magmaquiz.app.core.presentation.screens.Protocols

data class ServerConfig(
    val ip: String = "",
    val port: String = "",
    val protocol: Protocols = Protocols.HTTPS
)