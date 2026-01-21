package com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun defaultButtonColors(): ButtonColors {
    return ButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = MaterialTheme.colorScheme.background,
        disabledContentColor = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun selectedButtonColors(): ButtonColors {
    return ButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun notSelectedNavButtonColors(): ButtonColors {
    return ButtonColors(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        disabledContainerColor = MaterialTheme.colorScheme.background,
        disabledContentColor = MaterialTheme.colorScheme.onBackground
    )
}

    
