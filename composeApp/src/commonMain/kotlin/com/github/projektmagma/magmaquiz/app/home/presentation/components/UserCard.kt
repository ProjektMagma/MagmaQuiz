package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser

@Composable
fun UserCard(user: ForeignUser) {
    Column(
        modifier = Modifier
            .clickable {

            }
            .border(2.dp, MaterialTheme.colorScheme.onPrimary, MaterialTheme.shapes.large)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = user.userName)
            if (user.userProfilePicture == null)
                Icon(
                    modifier = Modifier.size(25.dp),
                    imageVector = Icons.Default.AccountCircle,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "SettingsButton",
                )
            else
                ProfilePictureIcon(user.userProfilePicture!!)
        }
    }
}