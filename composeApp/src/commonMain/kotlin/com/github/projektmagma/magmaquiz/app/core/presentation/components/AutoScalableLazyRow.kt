package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.empty_content
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun <T> AutoScalableLazyRow(
    itemList: List<T>,
    modifier: Modifier,
    key: ((T) -> Any)? = null,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    contentEmptyMessage: String = stringResource(Res.string.empty_content),
    content: @Composable (T) -> Unit
)