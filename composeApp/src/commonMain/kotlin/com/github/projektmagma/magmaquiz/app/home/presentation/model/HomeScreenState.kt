package com.github.projektmagma.magmaquiz.app.home.presentation.model

import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz

data class HomeScreenState(
    val recentQuizzes: List<Quiz> = emptyList(),
    val isLoadingMoreRecent: Boolean = false,
    
    val mostLikedQuizzes: List<Quiz> = emptyList(),
    val isLoadingMoreLiked: Boolean = false,
    
    val friendsQuizzes: List<Quiz> = emptyList(),
    val isLoadingMoreFriendsQuizzes: Boolean = false,
    
    val incomingFriends: List<ForeignUser> = emptyList(),
    val isLoadingMoreFriends: Boolean = false
)