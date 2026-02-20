package com.github.projektmagma.magmaquiz.app.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.projektmagma.magmaquiz.app.core.domain.Protocols
import com.github.projektmagma.magmaquiz.app.core.presentation.model.server.ServerConfigState
import kotlin.time.Clock

@Entity
data class ServerConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val ip: String = "",
    val port: String = "",
    val protocol: Protocols = Protocols.HTTPS,
    val isSelected: Boolean = true,
    val modifiedAt: Long = Clock.System.now().epochSeconds
)

fun ServerConfigState.toEntity(): ServerConfigEntity{
    return ServerConfigEntity(
        name = this.name,
        ip = this.ip,
        port =  this.port,
        protocol = this.protocol
    )
}

