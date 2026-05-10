package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
                    modifier = Modifier.fillMaxWidth().height(312.dp),
                    model = question.image
                )
            }

            Row {
                QuestionNumber(question.number)

                Text(text = question.content)
            }

            question.answerList.forEach { answer ->
                if (userAnswers != null) {
                    val answeredUsers = userAnswers
                        .filter { (_, value) -> value.contains(answer.id) }
                        .map { it.key }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnswerCard(answer)
                        OverlappingAvatars(users = answeredUsers)
                    }
                } else {
                    AnswerCard(answer)
                }
            }
        }
    }
}

@Composable
private fun OverlappingAvatars(
    users: List<ForeignUser>,
    maxVisible: Int = 6,
    size: Dp = 32.dp,
    overlap: Dp = 18.dp
) {
    if (users.isEmpty()) return

    val visibleUsers = users.take(maxVisible)
    val hasOverflow = users.size > maxVisible

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(size + overlap * (visibleUsers.size - 1))
                .height(size)
        ) {
            visibleUsers.forEachIndexed { index, user ->
                ProfilePictureIcon(
                    modifier = Modifier
                        .size(size)
                        .offset(x = overlap * index)
                        .zIndex((visibleUsers.size - index).toFloat()),
                    imageData = user.userProfilePicture,
                )
            }
        }
        if (hasOverflow) {
            Text(
                text = "...",
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}