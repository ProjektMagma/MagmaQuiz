package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun QuizNavigationBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier
) {
    BottomAppBar(
        modifier = modifier
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { (topLevelDestination, data) ->
            NavigationBarItem(
                selected = topLevelDestination == selectedKey,
                onClick = { onSelectKey(topLevelDestination) },
                icon = {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = stringResource(data.title),
                    )
                },
                label = { Text(text = stringResource(data.title)) },
            )
        }
    }
}