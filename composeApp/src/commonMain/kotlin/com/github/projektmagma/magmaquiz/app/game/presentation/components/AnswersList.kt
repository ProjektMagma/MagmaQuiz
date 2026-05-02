package com.github.projektmagma.magmaquiz.app.game.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.projektmagma.magmaquiz.app.game.presentation.model.play.AnswerState

@Composable
fun AnswersList(
    answers: List<AnswerState>,
    isAnswered: Boolean,
    onAnswerClick: (AnswerState) -> Unit
) {
    val labels = listOf("A", "B", "C", "D")
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        answers.forEachIndexed { index, answer ->
            AnswerButton(
                label = labels.getOrElse(index) { "" },
                answer = answer,
                isAnswered = isAnswered,
                onClick = { if (!isAnswered) onAnswerClick(answer) }
            )
        }
    }
}

@Composable
private fun AnswerButton(
    label: String,
    answer: AnswerState,
    isAnswered: Boolean,
    onClick: () -> Unit
) {
    val style = when {
        isAnswered && answer.isCorrect ->
            AnswerStyle(
                Color(0xFFE1F5EE),
                Color(0xFF1D9E75),
                Color(0xFF085041),
                Color(0xFF1D9E75),
                "✓"
            )

        isAnswered && answer.isSelected ->
            AnswerStyle(
                Color(0xFFFCEBEB),
                Color(0xFFA32D2D),
                Color(0xFF791F1F),
                Color(0xFFA32D2D),
                "✕"
            )

        isAnswered ->
            AnswerStyle(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.outlineVariant,
                MaterialTheme.colorScheme.onSurfaceVariant,
                MaterialTheme.colorScheme.outlineVariant,
                ""
            )

        else ->
            AnswerStyle(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.outline,
                MaterialTheme.colorScheme.onSurface,
                Color.Transparent,
                ""
            )
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = style.bg,
        border = BorderStroke(0.5.dp, style.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (isAnswered) style.indicator.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAnswered && style.symbol.isNotEmpty()) style.symbol else label,
                    fontSize = 12.sp,
                    color = if (isAnswered && style.symbol.isNotEmpty()) style.indicator else style.text,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = answer.content,
                style = MaterialTheme.typography.bodyLarge,
                color = style.text,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private data class AnswerStyle(
    val bg: Color,
    val border: Color,
    val text: Color,
    val indicator: Color,
    val symbol: String
)