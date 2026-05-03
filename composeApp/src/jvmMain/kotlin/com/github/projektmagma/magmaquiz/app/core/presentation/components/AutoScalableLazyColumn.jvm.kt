package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.github.projektmagma.magmaquiz.app.core.MainWindow
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
actual fun <T> AutoScalableLazyColumn(
    itemList: List<T>,
    key: ((T) -> Any)?,
    contentEmptyMessage: String,
    uiState: UiState,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    stickyHeader: @Composable (modifier: Modifier) -> Unit,
    skeletonContent: @Composable (() -> Unit)?,
    content: @Composable ((T) -> Unit)
) {
    val state = rememberLazyGridState()

    LaunchedEffect(itemList) {
        snapshotFlow { state.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == itemList.lastIndex + 1) {
                    onLoadMore()
                }
            }
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth(),
                columns = when (uiState) {
                    UiState.Success -> GridCells.Adaptive(450.dp)
                    else -> GridCells.FixedSize(MainWindow.windowState.size.width)
                },
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                state = state
            ) {
                stickyHeader {
                    stickyHeader(Modifier)
                }
                if (uiState == UiState.Success) {
                    items(items = itemList, key = key) {
                        content(it)
                    }
                    if (isLoadingMore) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                }
            }

            when (uiState) {
                UiState.Success -> {
                    if (itemList.isEmpty() && !isLoadingMore) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = contentEmptyMessage,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }

                is UiState.Error -> FullSizeErrorIndicator(message = uiState.errorMessage)
                UiState.Loading -> if (skeletonContent == null) {
                    FullSizeCircularProgressIndicator()
                } else {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxWidth(),
                        columns = GridCells.Adaptive(450.dp),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        items(5) {
                            skeletonContent()
                        }
                    }
                }
            }
        }

        VerticalScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 4.dp, bottom = 16.dp),
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