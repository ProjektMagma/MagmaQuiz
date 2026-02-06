package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.projektmagma.magmaquiz.app.core.util.convertLongSecondsToString
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.last_activity
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.util.UUID

@Composable
actual fun UserCard(
    user: ForeignUser,
    navigateToUserDetails: (id: UUID) -> Unit,
    inviteButtonText: StringResource,
    onInviteButtonClick: (id: UUID) -> Unit,
) {
    var isButtonClicked by rememberSaveable { mutableStateOf(true) }
    
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
                Text(text = "${stringResource(Res.string.last_activity)} ")
                Text(
                    text = convertLongSecondsToString(user.lastActivity),
                    fontWeight = FontWeight.Bold
                )
                Button(
                    shape = MaterialTheme.shapes.small,
                    enabled = isButtonClicked,
                    onClick = {
                        onInviteButtonClick(user.userId!!)
                        isButtonClicked = false
                    }
                ) { 
                    Text(stringResource(inviteButtonText))
                }
            }
            ProfilePictureIcon(user.userProfilePicture)
        }
    }
}