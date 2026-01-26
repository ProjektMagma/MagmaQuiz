package com.github.projektmagma.magmaquiz.app.home.presentation.components.quizzes

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        
        QuestionNumber(question.number)
        
        Text(text = question.content,)

        question.answerList.forEach { answer ->
            AnswerCard(
                answer
            )
        }
    }
}