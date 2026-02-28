package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
    val keyboardController = LocalSoftwareKeyboardController.current
    val state = rememberLazyListState()
    val yHeaderOffset by animateFloatAsState(
        targetValue = if (state.lastScrolledBackward || state.firstVisibleItemIndex == 0)
            0f
        else -150f,
        animationSpec = tween(durationMillis = 300, easing = EaseOut)
    )

    LaunchedEffect(state.isScrollInProgress) {
        keyboardController?.hide()
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
