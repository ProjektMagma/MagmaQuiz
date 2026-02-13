package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.runtime.Composable
import com.github.projektmagma.magmaquiz.app.home.presentation.UsersSharedViewModel
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import java.util.UUID

@Composable
expect fun UserCard(
    user: ForeignUser,
    usersSharedViewModel: UsersSharedViewModel,
    navigateToUserDetails: (id: UUID) -> Unit,
) 