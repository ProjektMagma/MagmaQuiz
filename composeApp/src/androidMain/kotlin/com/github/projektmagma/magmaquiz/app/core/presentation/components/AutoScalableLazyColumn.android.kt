package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun <T> AutoScalableLazyColumn(
    itemList: List<T>,
    key: ((T) -> Any)?,
    contentEmptyMessage: String,
    content: @Composable ((T) -> Unit)
) {
    if (itemList.isEmpty())
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = contentEmptyMessage,
                style = MaterialTheme.typography.titleLarge
            )
        }
    else
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxSize()
                .widthIn(max = 512.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            items(itemList, key = key) {
                content(it)
            }
        }
}