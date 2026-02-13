package com.github.projektmagma.magmaquiz.app.users.presentation.components

import androidx.compose.runtime.Composable
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import org.jetbrains.compose.resources.StringResource
import java.util.*

@Composable
expect fun UserCard(
    user: ForeignUser,
    navigateToUserDetails: (id: UUID) -> Unit,
    inviteButtonText: StringResource,
    onInviteButtonClick: (id: UUID) -> Unit,
)