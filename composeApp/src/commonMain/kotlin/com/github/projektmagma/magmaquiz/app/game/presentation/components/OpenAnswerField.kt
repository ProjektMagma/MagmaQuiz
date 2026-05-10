package com.github.projektmagma.magmaquiz.app.game.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.util.normalizedEquals
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.correct_answer
import magmaquiz.composeapp.generated.resources.correct_answer_content
import magmaquiz.composeapp.generated.resources.enter_answer
import magmaquiz.composeapp.generated.resources.wrong_answer
import org.jetbrains.compose.resources.stringResource

@Composable
fun OpenAnswerField(
    isAnswered: Boolean,
    correctAnswerContent: String,
    onSubmit: (String) -> Unit
) {
    var inputValue by remember { mutableStateOf("") }
    val isCorrect = isAnswered && correctAnswerContent.normalizedEquals(inputValue)

    val borderColor = when {
        !isAnswered -> MaterialTheme.colorScheme.outlineVariant
        isCorrect -> MaterialTheme.colorScheme.tertiary
        !isCorrect -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (isAnswered) {
                CorrectAnswerHint(
                    isCorrect = isCorrect,
                    correctAnswerContent = correctAnswerContent
                )
            }
            
            OutlinedTextField(
                value = inputValue,
                onValueChange = { if (!isAnswered) inputValue = it },
                enabled = !isAnswered,
                placeholder = {
                    Text(
                        text = stringResource(Res.string.enter_answer),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                trailingIcon = {
                    if (!isAnswered) {
                        IconButton(
                            onClick = { if (inputValue.isNotBlank()) onSubmit(inputValue) }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowRightAlt,
                                contentDescription = null,
                                tint = if (inputValue.isNotBlank())
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { if (inputValue.isNotBlank()) onSubmit(inputValue) }
                ),
                shape = MaterialTheme.shapes.large,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CorrectAnswerHint(
    isCorrect: Boolean,
    correctAnswerContent: String
) {
    Surface(
        modifier = Modifier.padding(bottom = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isCorrect)
            MaterialTheme.colorScheme.tertiaryContainer
        else
            MaterialTheme.colorScheme.errorContainer,
        border = BorderStroke(
            1.dp,
            if (isCorrect) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
        )
    ) {
        if (isCorrect) {
            Text(
                text = stringResource(Res.string.correct_answer),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Column(
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.wrong_answer),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )

                    Text(
                        text = stringResource(Res.string.correct_answer_content, correctAnswerContent),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        }
    }
}