package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.error_announcement
import magmaquiz.composeapp.generated.resources.try_again
import magmaquiz.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FullSizeErrorIndicator(message: StringResource = Res.string.unknown_error, onRetry: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.error_announcement),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(message),
            style = MaterialTheme.typography.titleMedium
        )
        if (onRetry != null)
            Button(onClick = { onRetry() }) {
                Text(text = stringResource(Res.string.try_again))
            }
    }
}
        