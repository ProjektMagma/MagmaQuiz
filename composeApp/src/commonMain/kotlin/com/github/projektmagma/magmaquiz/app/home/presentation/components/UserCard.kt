package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.runtime.Composable
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import org.jetbrains.compose.resources.StringResource
import java.util.UUID

@Composable
expect fun UserCard(
    user: ForeignUser,
    navigateToUserDetails: (id: UUID) -> Unit,
    inviteButtonText: StringResource,
    onInviteButtonClick: (id: UUID) -> Unit,
) 