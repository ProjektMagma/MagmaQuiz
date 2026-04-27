package com.github.projektmagma.magmaquiz.app.auth.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.forgot_password
import org.jetbrains.compose.resources.stringResource

@Composable
fun ForgotPasswordText(
    navigateToEmailSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .clickable(onClick = {
                navigateToEmailSettings()
            }
        ),
        text = stringResource(Res.string.forgot_password),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}