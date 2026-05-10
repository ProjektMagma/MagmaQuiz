package com.github.projektmagma.magmaquiz.app.home.presentation.model.rooms

data class RoomListState(
    val stringToSearch: String = "",
    val isLoadingMoreRooms: Boolean = false,
    val isRefreshing: Boolean = false
)
