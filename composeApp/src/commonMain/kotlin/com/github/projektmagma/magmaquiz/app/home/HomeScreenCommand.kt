package com.github.projektmagma.magmaquiz.app.home

import java.util.*

sealed interface HomeScreenCommand {
    data object RecentQuizzes : HomeScreenCommand
    data object MostLikedQuizzes : HomeScreenCommand
    data object IncomingFriends : HomeScreenCommand
    data object FriendsQuizzes : HomeScreenCommand
    data class ChangeFavorite(val id: UUID) : HomeScreenCommand
}