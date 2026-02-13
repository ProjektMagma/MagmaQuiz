package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.github.projektmagma.magmaquiz.app.core.presentation.components.NavButton
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun QuizNavigationBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier
) {
    TOP_LEVEL_DESTINATIONS.forEach { (topLevelDestination, data) ->
        NavButton(
            isCurrentRoute = selectedKey == topLevelDestination,
            onClick = {
                onSelectKey(topLevelDestination)
            },
            contentLabel = stringResource(data.title),
            contentIcon = data.icon
        )
    }
}