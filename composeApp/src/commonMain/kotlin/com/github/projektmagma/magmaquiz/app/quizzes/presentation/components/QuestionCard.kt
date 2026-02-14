package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.presentation.components.UniversalCardContainer
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.QuestionModel

@Composable
fun QuestionCard(
    question: QuestionModel,
    navigateToQuestionCreate: () -> Unit = { },
) {
    UniversalCardContainer(
        modifier = Modifier.padding(8.dp),
        onClick = { navigateToQuestionCreate() }
    ) {
        if (question.image != null) {
            QuizCoverImage(
                height = 312.dp,
                model = question.image
            )
        }

        QuestionNumber(question.number)

        Text(text = question.content)

        question.answerList.forEach { answer ->
            AnswerCard(
                answer
            )
        }
    }
}