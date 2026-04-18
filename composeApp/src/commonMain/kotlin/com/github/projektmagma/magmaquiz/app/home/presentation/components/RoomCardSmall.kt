package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.core.util.TimeConverter.toSeconds
import com.github.projektmagma.magmaquiz.shared.data.domain.RoomSettings
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.in_progress
import magmaquiz.composeapp.generated.resources.join
import magmaquiz.composeapp.generated.resources.waiting
import org.jetbrains.compose.resources.stringResource
import java.util.UUID

@Composable
fun RoomCardSmall(
    room: RoomSettings,
    onJoinClick: (UUID) -> Unit
) {
    val questionTimeSeconds = room.questionTimeInMillis.toSeconds()

    Surface(
        modifier = Modifier
            .widthIn(min = 220.dp, max = 340.dp)
            .padding(6.dp)
            .clickable { onJoinClick(room.roomId) },
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = room.roomName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContentImage(
                    imageData = room.currentQuiz.quizImage,
                    imageSize = 84.dp
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = room.currentQuiz.quizName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ProfilePictureIcon(
                            imageData = room.roomOwner.userProfilePicture,
                            size = 22.dp
                        )
                        Text(
                            text = room.roomOwner.userName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text("${room.connectedUsers}") },
                    leadingIcon = { Icon(Icons.Outlined.Group, contentDescription = null) }
                )
                AssistChip(
                    onClick = {},
                    label = { Text("${questionTimeSeconds}s") },
                    leadingIcon = { Icon(Icons.Outlined.Schedule, contentDescription = null) }
                )
                AssistChip(
                    onClick = {},
                    label = { Text(if (room.isInProgress) stringResource(Res.string.in_progress) else stringResource(Res.string.waiting)) },
                    leadingIcon = { Icon(Icons.Outlined.SportsEsports, contentDescription = null) }
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 0.5.dp
            )

            if (!room.isInProgress) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onJoinClick(room.roomId) }
                ) {
                    Text(stringResource(Res.string.join))
                }
            }
        }
    }
}
