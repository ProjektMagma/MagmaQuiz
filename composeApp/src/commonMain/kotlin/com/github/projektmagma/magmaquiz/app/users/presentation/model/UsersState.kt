package com.github.projektmagma.magmaquiz.app.users.presentation.model

import com.github.projektmagma.magmaquiz.app.users.presentation.model.list.UsersFilters
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser

data class UsersState(
    val username: String = "",
    val usersList: List<ForeignUser> = emptyList(),
    val usersFilter: UsersFilters = UsersFilters.None,
    val isLoadingMore: Boolean = false
)