package com.github.projektmagma.magmaquiz.app.home.presentation.model.rooms

import java.util.UUID

sealed interface RoomListCommand {
    data object LoadMore : RoomListCommand
    data object RefreshRooms : RoomListCommand
    data class StringToSearchChanged(val newString: String) : RoomListCommand
    data class SearchByName(val withDelay: Boolean) : RoomListCommand
    data class JoinRoom(val id: UUID) : RoomListCommand
}
