package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FavoriteButton
import com.github.projektmagma.magmaquiz.app.core.presentation.components.UniversalCardContainer
import com.github.projektmagma.magmaquiz.app.core.util.convertLongSecondsToString
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.created_at
import magmaquiz.composeapp.generated.resources.modified_at
import magmaquiz.composeapp.generated.resources.no_description
import org.jetbrains.compose.resources.stringResource
import java.util.UUID

@Composable
actual fun QuizCard(
    quiz: Quiz,
    navigateToQuizDetails: () -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
    navigateToQuizReviews: () -> Unit,
    changeFavoriteStatus: () -> Unit,
) {

    UniversalCardContainer(
        modifier = Modifier.padding(vertical = 4.dp),
        onClick = {
            navigateToQuizDetails()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UserProfileButton(
                        userName = quiz.quizCreator!!.userName,
                        profilePicture = quiz.quizCreator!!.userProfilePicture,
                        onClick = {
                            navigateToUserDetails(quiz.quizCreator!!.userId!!)
                        }
                    )
                }
                Row {
                    IconButton(
                        onClick = {
                            navigateToQuizReviews()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = null
                        )
                    }
                    FavoriteButton(
                        likesCount = quiz.likesCount,
                        isLiked = quiz.likedByYou,
                        changeFavoriteStatus = changeFavoriteStatus
                    )
                }
            }

            Text(
                text = quiz.quizName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = quiz.quizDescription.ifBlank { stringResource(Res.string.no_description) },
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            ContentImage(quiz.quizImage)

            StarRating(
                rating = quiz.averageRating
            )
            
            TagList(quiz.tagList)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${stringResource(Res.string.created_at)} ")
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
                Text(text = "${stringResource(Res.string.modified_at)} ")
                Text(
                    text = convertLongSecondsToString(quiz.modifiedAt),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}