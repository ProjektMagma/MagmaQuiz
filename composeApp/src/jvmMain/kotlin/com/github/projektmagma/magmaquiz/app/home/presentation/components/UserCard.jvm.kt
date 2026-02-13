package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.util.convertLongSecondsToString
import com.github.projektmagma.magmaquiz.app.home.presentation.UsersSharedViewModel
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.last_activity
import org.jetbrains.compose.resources.stringResource
import java.util.UUID

@Composable
actual fun UserCard(
    user: ForeignUser,
    usersSharedViewModel: UsersSharedViewModel,
    navigateToUserDetails: (id: UUID) -> Unit,
) {
    UniversalCardContainer(onClick = { navigateToUserDetails(user.userId!!) }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
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

                Spacer(modifier = Modifier.height(16.dp))

                FriendshipButtons(
                    user = user,
                    usersSharedViewModel = usersSharedViewModel
                )
            }
            ProfilePictureIcon(user.userProfilePicture)
        }
    }
}
