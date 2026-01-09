package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FilterButton(
    selected: Boolean = true,
    onClick: () -> Unit,
    contentLabel: String,
    contentIcon: ImageVector? = null,
) {
    val selectedFilterButtonsColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onPrimary
    )

    val notSelectedFilterButtonsColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = MaterialTheme.colorScheme.background,
        disabledContentColor = MaterialTheme.colorScheme.onBackground
    )

    Button(
        shape = MaterialTheme.shapes.large,
        colors = if (selected) selectedFilterButtonsColors
        else notSelectedFilterButtonsColors,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        onClick = {
            onClick()
        }) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = contentLabel,
                style = MaterialTheme.typography.titleMedium
            )
            if (contentIcon != null)
                Icon(
                    modifier = Modifier.size(25.dp),
                    imageVector = contentIcon,
                    tint = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                    contentDescription = contentLabel,
                )
        }

    }
}