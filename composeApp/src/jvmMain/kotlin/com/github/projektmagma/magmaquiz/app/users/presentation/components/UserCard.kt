package com.github.projektmagma.magmaquiz.app.users.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.core.presentation.components.UniversalCardContainer
import com.github.projektmagma.magmaquiz.app.core.util.convertLongSecondsToString
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.last_activity
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.util.*

@Composable
actual fun UserCard(
    user: ForeignUser,
    navigateToUserDetails: (id: UUID) -> Unit,
    inviteButtonText: StringResource,
    onInviteButtonClick: (id: UUID) -> Unit,
) {
    UniversalCardContainer(onClick = { navigateToUserDetails(user.userId!!) }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = user.userName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Text(text = "${stringResource(Res.string.last_activity)} ")
                    Text(
                        text = convertLongSecondsToString(user.lastActivity),
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        onInviteButtonClick(user.userId!!)
                    }
                ) {
                    Text(stringResource(inviteButtonText))
                }
            }
        }
        ProfilePictureIcon(user.userProfilePicture)
    }
}