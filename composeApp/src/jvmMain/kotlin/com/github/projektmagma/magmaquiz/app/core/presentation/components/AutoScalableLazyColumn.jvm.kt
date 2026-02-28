package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.github.projektmagma.magmaquiz.app.core.MainWindow
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState

@Composable
actual fun <T> AutoScalableLazyColumn(
    itemList: List<T>,
    key: ((T) -> Any)?,
    contentEmptyMessage: String,
    uiState: UiState,
    stickyHeader: @Composable (modifier: Modifier) -> Unit,
    content: @Composable ((T) -> Unit)
) {
    val state = rememberLazyGridState()

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
                if (uiState == UiState.Success)
                    items(itemList, key = key) {
                        content(it)
                    }

            }

            when (uiState) {
                UiState.Success -> {
                    if (itemList.isEmpty()) {
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
                UiState.Loading -> FullSizeCircularProgressIndicator()
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