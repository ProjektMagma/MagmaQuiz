package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UniversalCardContainer(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    lockClickable: Boolean = false,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceTint),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(disabledContentColor = contentColorFor(containerColor)),
        onClick = {
            onClick()
        },
        enabled = !lockClickable
    ) {
        content()
    }
}