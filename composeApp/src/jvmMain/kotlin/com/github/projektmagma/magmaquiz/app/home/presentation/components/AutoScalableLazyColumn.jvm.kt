package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
    LazyVerticalGrid(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize()
            .widthIn(max = 512.dp),
        columns = GridCells.Adaptive(450.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        items(itemList, key = key) {
            content(it)
        }
    }
}