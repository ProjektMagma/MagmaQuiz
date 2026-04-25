package com.github.projektmagma.magmaquiz.app.home.presentation.model.main

import java.util.UUID

sealed interface HomeScreenCommand {
    data object RecentQuizzes : HomeScreenCommand
    data object MostLikedQuizzes : HomeScreenCommand
    data object IncomingFriends : HomeScreenCommand
    data object FriendsQuizzes : HomeScreenCommand
    data class ChangeFavorite(val id: UUID) : HomeScreenCommand
}