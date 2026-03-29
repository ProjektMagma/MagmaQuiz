package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.empty_content
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun <T> AutoScalableLazyColumn(
    itemList: List<T>,
    key: ((T) -> Any)? = null,
    contentEmptyMessage: String = stringResource(Res.string.empty_content),
    uiState: UiState,
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit = {},
    stickyHeader: @Composable (modifier: Modifier) -> Unit,
    content: @Composable (T) -> Unit
)