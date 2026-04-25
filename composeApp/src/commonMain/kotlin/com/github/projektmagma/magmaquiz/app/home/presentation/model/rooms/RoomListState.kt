package com.github.projektmagma.magmaquiz.app.home.presentation.model.rooms

import com.github.projektmagma.magmaquiz.shared.data.domain.RoomSettings

data class RoomListState(
    val stringToSearch: String = "",
    val roomList: List<RoomSettings> = emptyList(),
    val isLoadingMoreRooms: Boolean = false,
    val isRefreshing: Boolean = false
)
