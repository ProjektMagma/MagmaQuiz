package com.github.projektmagma.magmaquiz.app.game.data

import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages

sealed interface WsEvent {
    data class OutGoing(val message: WebSocketMessages.OutgoingMessage) : WsEvent
    data class Closed(val reason: String?) : WsEvent
}