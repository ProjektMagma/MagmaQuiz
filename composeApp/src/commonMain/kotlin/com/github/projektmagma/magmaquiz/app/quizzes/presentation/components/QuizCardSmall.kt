package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FavoriteButton
import com.github.projektmagma.magmaquiz.app.core.presentation.components.UniversalCardContainer
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
    onDeleteClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    navigateToEditScreen: () -> Unit = {},
    navigateToUserDetails: (id: UUID) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    UniversalCardContainer(
        modifier = Modifier.widthIn(200.dp, 300.dp).padding(8.dp),
        onClick = { navigateToQuizDetails(quiz.id!!) }) {

        if (canEdit) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "more"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.edit)) },
                        onClick = {
                            expanded = false
                            onEditClick()
                            navigateToEditScreen()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.delete)) },
                        onClick = {
                            expanded = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier.heightIn(150.dp).padding(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ContentImage(quiz.quizImage, 128.dp)
            Text(
                text = quiz.quizName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = quiz.quizDescription,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (showUserButton) Arrangement.SpaceBetween else Arrangement.End
            ) {
                if (showUserButton)
                    UserProfileButton(
                        userName = quiz.quizCreator!!.userName,
                        profilePicture = quiz.quizCreator!!.userProfilePicture,
                        onClick = { navigateToUserDetails(quiz.quizCreator!!.userId!!) }
                    )

                FavoriteButton(
                    likesCount = quiz.likesCount,
                    isLiked = quiz.likedByYou,
                    changeFavoriteStatus = changeFavoriteStatus
                )
            }
        }
    }
}
