package com.github.projektmagma.magmaquiz.app.home.presentation.model

import java.util.UUID

sealed interface HomeScreenCommand {
    data object RecentQuizzes : HomeScreenCommand
    data object MostLikedQuizzes : HomeScreenCommand
    data object IncomingFriends : HomeScreenCommand
    data object FriendsQuizzes : HomeScreenCommand
    data object RoomList : HomeScreenCommand
    data class JoinGame(val id: UUID) : HomeScreenCommand
    data class ChangeFavorite(val id: UUID) : HomeScreenCommand
}