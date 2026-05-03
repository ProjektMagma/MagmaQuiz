package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.core.presentation.components.UniversalCardContainer
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.QuestionModel
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import java.util.UUID

@Composable
fun QuestionCard(
    question: QuestionModel,
    navigateToQuestionCreate: () -> Unit = { },
    userAnswers: List<Map.Entry<ForeignUser, List<UUID?>>>? = null,
    lockClickable: Boolean = false,
) {
    UniversalCardContainer(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        onClick = { navigateToQuestionCreate.invoke() },
        lockClickable = lockClickable
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (question.image != null) {
                QuizCoverImage(
                    height = 312.dp,
                    model = question.image
                )
            }

            QuestionNumber(question.number)

            Text(text = question.content)

            question.answerList.forEach { answer ->
                if (userAnswers != null) {
                    val answeredUsers = userAnswers.filter { (_, value) -> value.contains(answer.id) }.map { it.key }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        AnswerCard(answer)
                        Row(modifier = Modifier.padding(end = 8.dp)) {
                            answeredUsers.forEach { user ->
                                ProfilePictureIcon(
                                    imageData = user.userProfilePicture,
                                    size = 32.dp
                                )
                            }

                        }
                    }
                } else
                    AnswerCard(
                        answer
                    )
            }
        }
    }
}