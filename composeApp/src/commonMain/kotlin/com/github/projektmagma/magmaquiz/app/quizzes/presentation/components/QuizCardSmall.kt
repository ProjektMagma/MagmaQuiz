package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.projektmagma.magmaquiz.app.core.presentation.components.CommentButton
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FavoriteButton
import com.github.projektmagma.magmaquiz.app.core.presentation.components.StarRating
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.delete
import magmaquiz.composeapp.generated.resources.edit
import org.jetbrains.compose.resources.stringResource
import java.util.UUID

@Composable
fun QuizCardSmall(
    quiz: Quiz,
    changeFavoriteStatus: () -> Unit,
    navigateToQuizDetails: (id: UUID) -> Unit,
    showUserButton: Boolean = true,
    canEdit: Boolean = false,
    navigateToQuizReviews: () -> Unit,
    onDeleteClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    navigateToEditScreen: () -> Unit = {},
    navigateToUserDetails: (id: UUID) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .widthIn(min = 180.dp, max = 320.dp)
            .padding(6.dp)
            .clickable { navigateToQuizDetails(quiz.id!!) },
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showUserButton && quiz.quizCreator != null) {
                UserProfileButton(
                    userName = quiz.quizCreator!!.userName,
                    profilePicture = quiz.quizCreator!!.userProfilePicture,
                    onClick = { navigateToUserDetails(quiz.quizCreator!!.userId!!) }
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ContentImage(
                    imageData = quiz.quizImage,
                    imageSize = 100.dp
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = quiz.quizName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = quiz.quizDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }

                if (canEdit) {
                    Box {
                        IconButton(
                            onClick = { expanded = !expanded },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            offset = DpOffset((-60).dp, 0.dp)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(Res.string.edit),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    onEditClick()
                                    navigateToEditScreen()
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(Res.string.delete),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }
            
            StarRating(rating = quiz.averageRating)
            
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 0.5.dp
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start
                ) { 
                    QuizVisibilityIcon(quiz.visibility)
                }
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CommentButton(
                        navigateToQuizReviews = { navigateToQuizReviews() },
                        reviewCount = quiz.reviewCount
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    FavoriteButton(
                        likesCount = quiz.likesCount,
                        isLiked = quiz.likedByYou,
                        changeFavoriteStatus = changeFavoriteStatus
                    )
                }
            }
        }
    }
}