package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.projektmagma.magmaquiz.app.home.presentation.model.AnswerState
import com.github.projektmagma.magmaquiz.app.home.presentation.model.QuestionState

@Composable
fun QuestionCard(
    question: QuestionState,
    onQuestionContentChange: (String) -> Unit,
    onAnswerContentChange: (AnswerState) -> Unit,
    onAddNewAnswer: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        
        Text(question.number.toString())
        OutlinedTextField(
            value = question.content,
            onValueChange = {
                onQuestionContentChange(it)
            }
        )
        
        question.answerList.forEach { answer ->
            AnswerCard(
                answer,
                onValueChange = {
                    onAnswerContentChange(answer.copy(content = it))
                }
            )
        }
       
        IconButton(
            onClick = {
                onAddNewAnswer()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Dodaj"
            )
        }
    }
}