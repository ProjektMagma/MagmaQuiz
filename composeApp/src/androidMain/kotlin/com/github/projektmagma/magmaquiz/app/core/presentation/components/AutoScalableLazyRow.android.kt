package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
actual fun <T> AutoScalableLazyRow(
    itemList: List<T>,
    modifier: Modifier,
    key: ((T) -> Any)?,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    contentEmptyMessage: String,
    content: @Composable ((T) -> Unit)
) {
    val state = rememberLazyListState()

    LaunchedEffect(itemList){
        snapshotFlow { state.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == itemList.lastIndex) {
                    onLoadMore()
                }
            }
    }

    if (itemList.isEmpty())
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = contentEmptyMessage,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    else
        LazyRow(
            modifier = modifier,
            state = state
        ) {
            items(itemList, key = key) {
                content(it)
            }
            item {
                if (isLoadingMore) {
                    Box(
                        modifier = Modifier.fillParentMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
}