package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WideTonalButton(
    modifier: Modifier = Modifier,
    text: StringResource,
    icon: ImageVector? = null,
    action: () -> Unit,
) {
    FilledTonalButton(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        onClick = { action() }
    ) {

        if (icon != null)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                Text(stringResource(text))
                Icon(icon, contentDescription = null)
            }
        else
            Text(stringResource(text))
    }
}
