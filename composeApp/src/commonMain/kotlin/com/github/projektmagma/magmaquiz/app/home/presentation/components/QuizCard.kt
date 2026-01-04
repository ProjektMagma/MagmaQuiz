package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import java.util.UUID

@Composable
fun QuizCard(
    quiz: Quiz,
    navigateToQuizDetails: (id: UUID) -> Unit
) {
    Column(
        modifier = Modifier
            .border(2.dp, MaterialTheme.colorScheme.onPrimary, MaterialTheme.shapes.large)
            .padding(8.dp)
            .clickable(
                onClick = {
                    navigateToQuizDetails(quiz.id!!) // TODO id moze byc nullem?
                }
            )
    ) {
        Text(text = quiz.quizName)
        Text(text = quiz.quizDescription ?: "null")
        Text(text = quiz.isPublic.toString())
        Text(text = quiz.quizCreatorName ?: "null")
        Text(text = quiz.likesCount.toString())
        Text(text = quiz.likedByYou.toString())
    }
}