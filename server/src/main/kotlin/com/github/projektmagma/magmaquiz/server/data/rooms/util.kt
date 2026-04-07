package com.github.projektmagma.magmaquiz.server.data.rooms

import io.ktor.websocket.*
import kotlinx.serialization.json.Json

inline fun <reified T> encodedFrame(data: T): Frame {
    return Frame.Text(Json.encodeToString(data))
}