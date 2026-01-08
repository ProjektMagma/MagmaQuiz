package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun <T> AutoScalableLazyColumn(
    itemList: List<T>,
    content: @Composable ((T) -> Unit)
) {
    LazyVerticalGrid(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxSize()
            .widthIn(max = 512.dp),
        columns = GridCells.Adaptive(400.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        items(itemList) {
            content(it)
        }
    }
}