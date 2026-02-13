package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.runtime.Composable
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.empty_content
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun <T> AutoScalableLazyColumn(
    itemList: List<T>,
    key: ((T) -> Any)? = null,
    contentEmptyMessage: String = stringResource(Res.string.empty_content),
    content: @Composable (T) -> Unit
) 