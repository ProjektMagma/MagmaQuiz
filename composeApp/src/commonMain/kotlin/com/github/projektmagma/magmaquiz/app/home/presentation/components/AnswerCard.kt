package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.home.presentation.model.AnswerState

@Composable
fun AnswerCard(
    answer: AnswerState,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        OutlinedTextField(
            value = answer.content,
            onValueChange = {
                onValueChange(it)
            },
            label = { Text(text = "tresc odpowiedzi") }
        )
    }
}