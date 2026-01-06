package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import java.util.UUID

@Composable
fun QuizCard(
    quiz: Quiz,
    navigateToQuizDetails: (id: UUID) -> Unit,
    changeFavoriteStatus: () -> Unit
) {
    var isLiked by remember { mutableStateOf(quiz.likedByYou) }
    
    Column(
        modifier = Modifier
            .clickable {
                navigateToQuizDetails(quiz.id!!)
            }
            .border(2.dp, MaterialTheme.colorScheme.onPrimary, MaterialTheme.shapes.large)
            .padding(8.dp)
    ) {
        Text(text = quiz.quizName)
        Text(text = quiz.quizDescription ?: "null")
        Text(text = quiz.isPublic.toString())
        Text(text = quiz.quizCreatorName ?: "null")
        Text(text = quiz.likesCount.toString())
        Text(text = isLiked.toString())
        IconButton(
            onClick = {
                changeFavoriteStatus()
                isLiked = !isLiked!!
            }
        ) {
            Icon(
                imageVector = if (isLiked == true) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                contentDescription = "Gwiazda do polubiania"
            )
        }
    }
}