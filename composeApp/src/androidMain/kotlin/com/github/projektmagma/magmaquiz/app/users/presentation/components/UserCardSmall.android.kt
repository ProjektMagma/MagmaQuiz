package com.github.projektmagma.magmaquiz.app.users.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FriendshipButtons
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.core.presentation.components.UniversalCardContainer
import com.github.projektmagma.magmaquiz.app.core.util.convertLongSecondsToString
import com.github.projektmagma.magmaquiz.app.users.presentation.UsersSharedViewModel
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.last_activity
import org.jetbrains.compose.resources.stringResource
import java.util.*

@Composable
actual fun UserCardSmall(
    user: ForeignUser,
    usersSharedViewModel: UsersSharedViewModel,
    navigateToUserDetails: (id: UUID) -> Unit
) {
    UniversalCardContainer(
        modifier = Modifier
            .width(200.dp)
            .padding(8.dp),
        onClick = { navigateToUserDetails(user.userId!!) }) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = user.userName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            ProfilePictureIcon(user.userProfilePicture)

            Text(
                text = stringResource(Res.string.last_activity),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = convertLongSecondsToString(user.lastActivity),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )

            FriendshipButtons(
                user = user,
                usersSharedViewModel = usersSharedViewModel,
                useColumn = true
            )
        }
    }
}
