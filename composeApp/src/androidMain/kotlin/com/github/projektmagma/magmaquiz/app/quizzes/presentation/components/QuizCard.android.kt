package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.layout.*
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
import java.util.*

@Composable
actual fun QuizCard(
    quiz: Quiz,
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
    changeFavoriteStatus: () -> Unit,
) {


    UniversalCardContainer(
        modifier = Modifier.padding(8.dp),
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
                    UserProfileButton(
                        userName = quiz.quizCreator!!.userName,
                        profilePicture = quiz.quizCreator!!.userProfilePicture,
                        onClick = {
                            navigateToUserDetails(quiz.quizCreator!!.userId!!)
                        }
                    )
                }
                FavoriteButton(
                    likesCount = quiz.likesCount,
                    isLiked = quiz.likedByYou,
                    changeFavoriteStatus = changeFavoriteStatus
                )
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