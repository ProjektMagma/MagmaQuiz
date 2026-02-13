package com.github.projektmagma.magmaquiz.app.home.presentation.model.users

import com.github.projektmagma.magmaquiz.app.home.presentation.model.users.list.UsersFilters
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser

data class UsersState(
    val username: String = "",
    val usersList: List<ForeignUser> = emptyList(),
    val usersFilter: UsersFilters = UsersFilters.None
)