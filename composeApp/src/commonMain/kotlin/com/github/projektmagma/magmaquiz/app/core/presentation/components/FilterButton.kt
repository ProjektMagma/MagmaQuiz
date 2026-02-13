package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme.defaultButtonColors
import com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme.selectedButtonColors

@Composable
fun FilterButton(
    selected: Boolean = true,
    onClick: () -> Unit,
    contentLabel: String,
    contentIcon: ImageVector? = null,
) {

    ButtonWithIcon(
        colors = if (selected) selectedButtonColors()
        else defaultButtonColors(),
        onClick = onClick,
        contentLabel = contentLabel,
        contentIcon = contentIcon,
    )
}