package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.NavigationState
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Navigator
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.QuizNavigationBar
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.components.AnimatedVisibilityFloatingButton
import com.github.projektmagma.magmaquiz.app.home.presentation.components.ProfilePictureIcon
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.greeting
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun MainNavMenu(
    navigator: Navigator,
    navigationState: NavigationState,
    navigateToUserProfile: () -> Unit,
    content: @Composable (() -> Unit)
) {
    val viewModel = koinViewModel<AuthViewModel>()
    val thisUser = viewModel.thisUser.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AnimatedVisibilityFloatingButton(
                isShown = navigationState.topLevelRoute == Route.Menus.Quizzes.QuizList,
                animationPositionMultiplier = 4,
                onClick = {
                    navigator.navigate(Route.Menus.Quizzes.CreateQuiz)
                }
            )
        },
        bottomBar = {
            QuizNavigationBar(
                selectedKey = navigationState.topLevelRoute,
                onSelectKey = {
                    navigator.navigate(it)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(Res.string.greeting)}, ${thisUser.value!!.userName}!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { navigateToUserProfile() },
                ) {
                    ProfilePictureIcon(thisUser.value!!.userProfilePicture)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}