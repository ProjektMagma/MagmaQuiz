package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme.defaultButtonColors

@Composable
fun ButtonWithIcon(
    onClick: () -> Unit,
    contentLabel: String,
    colors: ButtonColors = defaultButtonColors(),
    contentIcon: ImageVector? = null,
) {
    Button(
        shape = MaterialTheme.shapes.large,
        colors = colors,
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
                style = MaterialTheme.typography.bodyLarge
            )
            if (contentIcon != null)
                Icon(
                    modifier = Modifier.size(25.dp),
                    imageVector = contentIcon,
                    tint = colors.contentColor,
                    contentDescription = contentLabel,
                )
        }

    }
}