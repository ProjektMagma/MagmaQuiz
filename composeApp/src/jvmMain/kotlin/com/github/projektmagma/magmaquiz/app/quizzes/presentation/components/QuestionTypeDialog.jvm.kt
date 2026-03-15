package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.choose_type
import magmaquiz.composeapp.generated.resources.multi_answer
import magmaquiz.composeapp.generated.resources.single_answer
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun QuestionTypeDialog(
    onClick: (Boolean) -> Unit,
    changeDialogVisibility: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { changeDialogVisibility() },
        title = {
            Text(stringResource(Res.string.choose_type))
        },
        confirmButton = {
            Button(
                onClick = { onClick(false) }
            ) {
                Text(stringResource(Res.string.single_answer),)
            }
        },
        dismissButton = {
            Button(
                onClick = { onClick(true) }
            ) {
                Text(stringResource(Res.string.multi_answer),)
            }
        },
    )
}