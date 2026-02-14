package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import java.util.*

@Composable
fun QuizCardSmall(
    quiz: Quiz,
    showUserButton: Boolean = true,
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit = {},
    changeFavoriteStatus: () -> Unit
) {

    UniversalCardContainer(
        modifier = Modifier.widthIn(200.dp, 300.dp).padding(8.dp),
        onClick = { navigateToQuizDetails(quiz.id!!) }) {


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
