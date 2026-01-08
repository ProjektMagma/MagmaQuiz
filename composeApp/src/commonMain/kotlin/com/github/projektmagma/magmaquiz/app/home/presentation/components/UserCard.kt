package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import java.util.UUID

@Composable
fun UserCard(
    user: ForeignUser,
    navigateToUserDetails: (id: UUID) -> Unit,
) {
    UniversalCardContainer(onClick = { navigateToUserDetails(user.userId!!) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = user.userName)
            ProfilePictureIcon(user.userProfilePicture)
        }
    }
}