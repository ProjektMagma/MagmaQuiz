package com.github.projektmagma.magmaquiz.app.users.presentation.components

import androidx.compose.runtime.Composable
import com.github.projektmagma.magmaquiz.app.users.presentation.UsersSharedViewModel
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import java.util.*

@Composable
expect fun UserCardSmall(
    user: ForeignUser,
    usersSharedViewModel: UsersSharedViewModel,
    navigateToUserDetails: (id: UUID) -> Unit,
)