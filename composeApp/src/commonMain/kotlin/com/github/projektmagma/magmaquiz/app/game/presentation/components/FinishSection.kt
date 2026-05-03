package com.github.projektmagma.magmaquiz.app.game.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuestionNumber
import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.all
import magmaquiz.composeapp.generated.resources.back
import magmaquiz.composeapp.generated.resources.correct
import magmaquiz.composeapp.generated.resources.correct_answer
import magmaquiz.composeapp.generated.resources.percent_correct
import magmaquiz.composeapp.generated.resources.wrong
import magmaquiz.composeapp.generated.resources.your_answer
import org.jetbrains.compose.resources.stringResource
import java.util.UUID
import kotlin.math.roundToInt

@Composable
fun FinishSection(
    answersMap: Map<UUID, String>,
    questions: List<Question>,
    score: Int,
    total: Int,
    onBack: () -> Unit
) {
    val pct = if (total > 0) ((score.toDouble() / total) * 100).roundToInt() else 0
    
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$score",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "/ $total",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = stringResource(Res.string.percent_correct, pct),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "$score",
                    label = stringResource(Res.string.correct),
                    color = MaterialTheme.colorScheme.tertiary,
                    bg = MaterialTheme.colorScheme.tertiaryContainer
                )
                Box(
                    modifier = Modifier
                        .width(0.5.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                StatItem(
                    value = "${total - score}",
                    label = stringResource(Res.string.wrong),
                    color = MaterialTheme.colorScheme.error,
                    bg = MaterialTheme.colorScheme.errorContainer
                )
                Box(
                    modifier = Modifier
                        .width(0.5.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                StatItem(
                    value = "$total",
                    label = stringResource(Res.string.all),
                    color = MaterialTheme.colorScheme.primary,
                    bg = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
        
        questions.forEachIndexed { index, question ->
            val selectedAnswer = answersMap[question.id] ?: "-"
            val correctAnswer = question.answerList.find { it.isCorrect }
            val isCorrect = question.answerList.find { it.answerContent.equals(selectedAnswer, ignoreCase = true) }?.isCorrect == true

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    0.5.dp,
                    if (isCorrect) Color(0xFF1D9E75) else MaterialTheme.colorScheme.error
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        QuestionNumber(index + 1)
                        Text(
                            text = question.questionContent,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                    
                    ReviewAnswerRow(
                        label = stringResource(Res.string.your_answer),
                        content = selectedAnswer,
                        isCorrect = isCorrect,
                        showIcon = true
                    )
                    
                    if (!isCorrect) {
                        ReviewAnswerRow(
                            label = stringResource(Res.string.correct_answer),
                            content = correctAnswer?.answerContent ?: "—",
                            isCorrect = true,
                            showIcon = false
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = stringResource(Res.string.back))
        }
    }
}

@Composable
private fun ReviewAnswerRow(
    label: String,
    content: String,
    isCorrect: Boolean,
    showIcon: Boolean
) {
    val color = if (isCorrect) Color(0xFF1D9E75) else MaterialTheme.colorScheme.error
    val bg = if (isCorrect) Color(0xFFE1F5EE) else Color(0xFFFCEBEB)
    val symbol = if (isCorrect) "✓" else "✕"

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(96.dp)
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = bg,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showIcon) {
                    Text(text = symbol, fontSize = 12.sp, color = color)
                }
                Text(
                    text = content,
                    fontSize = 14.sp,
                    color = color,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color, bg: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}