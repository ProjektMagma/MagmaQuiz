package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.runtime.Composable

@Composable
expect fun <T> AutoScalableLazyColumn(itemList: List<T>, content: @Composable (T) -> Unit) 