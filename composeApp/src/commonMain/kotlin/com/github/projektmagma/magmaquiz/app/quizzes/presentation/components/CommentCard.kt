package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizReview
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommentCard(
    review: QuizReview,
    showOptions: Boolean,
    modifier: Modifier = Modifier,
    deleteReview: () -> Unit
) {
    var dropDownVisible by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfilePictureIcon(
                    imageData = review.author?.userProfilePicture,
                    size = 40.dp
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = review.author?.userName ?: "Unknown",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            val isSelected = index < review.rating
                            Icon(
                                imageVector = if (isSelected) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFFFFB400) else MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                if (showOptions) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Row {
                            IconButton(
                                onClick = {
                                    dropDownVisible = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null
                                )
                            }

                            DropdownMenu(
                                expanded = dropDownVisible,
                                onDismissRequest = { dropDownVisible = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.delete)) },
                                    onClick = {
                                        deleteReview()
                                        dropDownVisible = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}