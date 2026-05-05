package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
    val keyboardController = LocalSoftwareKeyboardController.current
    val state = rememberLazyListState()

    var firstVisibleIndex by remember { mutableIntStateOf(0) }

    val yHeaderOffset by animateFloatAsState(
        targetValue = if (state.lastScrolledBackward || firstVisibleIndex == 0
        )
            0f
        else -150f,
        animationSpec = tween(durationMillis = 300, easing = EaseOut)
    )

    LaunchedEffect(state.isScrollInProgress) {
        snapshotFlow { state.firstVisibleItemIndex }
            .collect { firstVisibleIndex = it }
        keyboardController?.hide()
    }

    LaunchedEffect(itemList) {
        snapshotFlow { state.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == itemList.lastIndex + 1) {
                    onLoadMore()
                }
            }
    }

    LazyColumn(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .fillMaxWidth(),
        state = state,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        stickyHeader {
            stickyHeader(Modifier.offset(y = yHeaderOffset.dp))
        }

        if (uiState == UiState.Success) {
            items(items = itemList, key = key) { item ->
                content(item)
            }
            if (isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(64.dp)) }
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
            Column(Modifier.fillMaxWidth()) {
                repeat(5) {
                    skeletonContent()
                }
            }
        }
    }
}
