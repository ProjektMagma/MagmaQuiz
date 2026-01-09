package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.projektmagma.magmaquiz.app.core.util.convertLongSecondsToString
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import java.util.UUID

@Composable
actual fun UserCard(
    user: ForeignUser,
    navigateToUserDetails: (id: UUID) -> Unit
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
                    Text(text = "Ostatnia aktywność ")
                    Text(
                        text = convertLongSecondsToString(user.lastActivity),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            ProfilePictureIcon(user.userProfilePicture)

        }
    }
}
