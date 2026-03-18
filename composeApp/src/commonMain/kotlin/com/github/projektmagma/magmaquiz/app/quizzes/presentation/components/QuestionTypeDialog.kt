package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.runtime.Composable

@Composable
expect fun QuestionTypeDialog(
    onClick: (Boolean) -> Unit,
    changeDialogVisibility: () -> Unit
)