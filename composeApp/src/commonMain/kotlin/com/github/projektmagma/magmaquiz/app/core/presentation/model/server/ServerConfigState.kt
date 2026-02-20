package com.github.projektmagma.magmaquiz.app.core.presentation.model.server

import com.github.projektmagma.magmaquiz.app.core.data.database.ServerConfigEntity
import com.github.projektmagma.magmaquiz.app.core.domain.Protocols

data class ServerConfigState(
    val id: Int = 0,
    val name: String = "",
    val ip: String = "",
    val port: String = "",
    val protocol: Protocols = Protocols.HTTPS,
    val isEdited: Boolean = false
)

fun ServerConfigEntity.toState(): ServerConfigState {
    return ServerConfigState(
        id = this.id,
        name = this.name,
        ip = this.ip,
        port = this.port,
        protocol = this.protocol
    )
}