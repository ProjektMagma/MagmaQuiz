package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun <T> AutoScalableLazyRow(
    itemList: List<T>,
    key: ((T) -> Any)?,
    contentEmptyMessage: String,
    content: @Composable ((T) -> Unit)
) {

    val state = rememberLazyListState()

    if (itemList.isEmpty())
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = contentEmptyMessage,
                style = MaterialTheme.typography.titleSmall
            )
        }
    else

        Column(modifier = Modifier.fillMaxWidth()) {
            LazyRow(modifier = Modifier.fillMaxWidth(), state = state) {
                items(itemList, key = key) {
                    content(it)
                }
            }

            HorizontalScrollbar(
                modifier = Modifier.fillMaxWidth(),
                adapter = rememberScrollbarAdapter(scrollState = state),
                style = ScrollbarStyle(
                    minimalHeight = 10.dp,
                    thickness = 10.dp,
                    shape = MaterialTheme.shapes.medium,
                    hoverDurationMillis = 500,
                    unhoverColor = MaterialTheme.colorScheme.surfaceContainer,
                    hoverColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
}