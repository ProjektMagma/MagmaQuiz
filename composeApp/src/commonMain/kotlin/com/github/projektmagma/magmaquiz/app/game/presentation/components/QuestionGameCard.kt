package com.github.projektmagma.magmaquiz.app.game.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage

@Composable
fun QuestionGameCard(imageData: ByteArray?, content: String) {
    if (imageData != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            ContentImage(
                imageData = imageData,
            )
        }
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            Text(
                text = content,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(14.dp),
                lineHeight = 24.sp
            )
        }
    }
}