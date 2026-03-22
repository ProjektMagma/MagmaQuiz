package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.shared.data.domain.Tag

@Composable
fun TagList(
    tagList: List<Tag>,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        maxLines = 2
    ) {
        tagList.forEach {
            InputChip(
                selected = false,
                onClick = {},
                label = { Text(it.tagName) },
            )
        }
    }
}