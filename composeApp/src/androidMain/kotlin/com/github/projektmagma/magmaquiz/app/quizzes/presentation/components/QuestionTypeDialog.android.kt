package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            changeDialogVisibility()
        },
        sheetState = modalBottomSheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = stringResource(Res.string.choose_type))

            Button(
                onClick = { onClick(false) }
            ) {
                Text(text = stringResource(Res.string.single_answer))
            }

            Button(
                onClick = { onClick(true) }
            ) {
                Text(text = stringResource(Res.string.multi_answer))
            }
        }
    }
}