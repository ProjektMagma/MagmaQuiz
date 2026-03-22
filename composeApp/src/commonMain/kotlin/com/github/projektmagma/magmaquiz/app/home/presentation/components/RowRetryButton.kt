package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.try_again
import org.jetbrains.compose.resources.stringResource

@Composable
fun RowRetryButton(
    message: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = message)

        Button(
            onClick = { onClick() }
        ) {
            Text(text = stringResource(Res.string.try_again))
        }
    }
}