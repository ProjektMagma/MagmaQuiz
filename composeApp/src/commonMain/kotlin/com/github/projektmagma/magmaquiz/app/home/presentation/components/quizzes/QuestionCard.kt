package com.github.projektmagma.magmaquiz.app.home.presentation.components.quizzes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.home.presentation.components.QuizCoverImage
import com.github.projektmagma.magmaquiz.app.home.presentation.components.UniversalCardContainer
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.create.QuestionModel

@Composable
fun QuestionCard(
    question: QuestionModel,
    navigateToQuestionCreate: () -> Unit = { },
) {
    UniversalCardContainer(
        onClick = { navigateToQuestionCreate() }
    ) {
        if (question.image != null) {
            QuizCoverImage(
                height = 312.dp,
                model = question.image
            )
        }
        
        Text(
            modifier = Modifier
                .padding(4.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            text = question.number.toString()
        )
        
        Text(text = question.content,)

        question.answerList.forEach { answer ->
            AnswerCard(
                answer
            )
        }
    }
}