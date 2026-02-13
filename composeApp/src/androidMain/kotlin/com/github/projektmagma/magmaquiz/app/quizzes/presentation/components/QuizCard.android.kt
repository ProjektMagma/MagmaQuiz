package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.core.presentation.components.UniversalCardContainer
import com.github.projektmagma.magmaquiz.app.core.util.convertLongSecondsToString
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import java.util.*

@Composable
actual fun QuizCard(
    quiz: Quiz,
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
    changeFavoriteStatus: () -> Unit,
    modifier: Modifier 
) {
    var isLiked by remember { mutableStateOf(quiz.likedByYou) }
    var likesCount by remember { mutableIntStateOf(quiz.likesCount) }

    UniversalCardContainer(
        modifier = modifier,
        onClick = {
            navigateToQuizDetails(quiz.id!!)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.shapes.medium
                            )
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                navigateToUserDetails(quiz.quizCreator!!.userId!!)
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = quiz.quizCreator!!.userName,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        ProfilePictureIcon(quiz.quizCreator!!.userProfilePicture, 25.dp)
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = "$likesCount",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            changeFavoriteStatus()
                            isLiked = !isLiked
                            if (isLiked) likesCount++
                            else likesCount--

                        }
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            tint = Color(0xfff498ae),
                            contentDescription = "FavoriteButton"
                        )

                    }
                }
            }

            Text(
                text = quiz.quizName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = quiz.quizDescription.ifBlank { "Nie podano opisu" },
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            ContentImage(quiz.quizImage)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Data stworzenia ")
                Text(
                    text = convertLongSecondsToString(quiz.createdAt),
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Data modyfikacji ")
                Text(
                    text = convertLongSecondsToString(quiz.modifiedAt),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}