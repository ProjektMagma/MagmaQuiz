package com.github.projektmagma.magmaquiz.presentation.model.server

import com.github.projektmagma.magmaquiz.presentation.screens.Protocols

data class ServerConfig(
    val ip: String = "menito.eu",
    val port: String = "8080",
    val protocol: Protocols = Protocols.HTTPS
)