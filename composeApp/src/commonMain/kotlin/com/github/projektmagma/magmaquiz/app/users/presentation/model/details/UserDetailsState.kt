package com.github.projektmagma.magmaquiz.app.users.presentation.model.details

import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User

data class UserDetailsState(
    val selectedTabIndex: Int = 0,
    val quizzes: List<Quiz>? = emptyList(),
    val user: User? = null,
    val isLoadingMore: Boolean = false
)