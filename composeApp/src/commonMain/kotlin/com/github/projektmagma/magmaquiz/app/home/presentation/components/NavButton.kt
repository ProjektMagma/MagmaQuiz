package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme.notSelectedNavButtonColors
import com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme.selectedButtonColors


@Composable
fun NavButton(
    isCurrentRoute: Boolean = true,
    onClick: () -> Unit,
    contentLabel: String,
    contentIcon: ImageVector? = null,
) {


    Button(
        colors = if (isCurrentRoute) selectedButtonColors()
        else notSelectedNavButtonColors(),
        border = if (isCurrentRoute) null
        else BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
        onClick = {
            onClick()
        }) {
        Row(
            modifier = Modifier.height(32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                    tint = if (isCurrentRoute) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onBackground,
                    contentDescription = contentLabel,
                )
        }

    }
}