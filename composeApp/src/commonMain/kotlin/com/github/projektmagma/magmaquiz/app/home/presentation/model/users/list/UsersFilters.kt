package com.github.projektmagma.magmaquiz.app.home.presentation.model.users.list

interface UsersFilters {
    data object None: UsersFilters
    data object Friends: UsersFilters
    data object SentInvitations: UsersFilters
    data object IncomingInvitations: UsersFilters
}