package com.github.projektmagma.magmaquiz.app.auth.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigationAuthText(
    text1: String,
    text2: String,
    navigationAction: () -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text1)
        Spacer(modifier = Modifier.padding(horizontal = 2.dp))
        Text(
            modifier = Modifier
                .clickable(onClick = { navigationAction() }),
            text = text2,
            style = MaterialTheme.typography.labelMedium
        )
    }
}