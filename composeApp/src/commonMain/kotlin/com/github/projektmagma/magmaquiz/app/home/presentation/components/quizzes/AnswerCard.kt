package com.github.projektmagma.magmaquiz.app.home.presentation.components.quizzes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.AnswerModel
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.correct_answer
import magmaquiz.composeapp.generated.resources.wrong_asnwer
import org.jetbrains.compose.resources.stringResource

@Composable
fun AnswerCard(
    answer: AnswerModel,
) {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        if (answer.isCorrect) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = stringResource(Res.string.correct_answer)
            )
        } else { 
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(Res.string.wrong_asnwer)
            )
        }
        Text(
            text = answer.content,
        )
    }
}