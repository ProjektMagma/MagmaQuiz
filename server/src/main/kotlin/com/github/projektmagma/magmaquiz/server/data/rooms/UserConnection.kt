package com.github.projektmagma.magmaquiz.server.data.rooms

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import io.ktor.websocket.*

data class UserConnection(
    val userEntity: UserEntity,
    val webSocketSession: WebSocketSession
)
