package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.di.Navigator
import com.github.projektmagma.magmaquiz.app.core.presentation.RootViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.AuthState
import com.github.projektmagma.magmaquiz.app.home.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.home.presentation.navigation.CustomWindowDraggableArea
import com.github.projektmagma.magmaquiz.app.home.presentation.navigation.MainNavMenu
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    rootViewModel: RootViewModel = koinViewModel()
) {
    val navigator: Navigator = koinInject()
    val authState by rootViewModel.state.collectAsStateWithLifecycle()
    
    when (authState) {
        AuthState.Loading -> {
            Column(modifier = Modifier.fillMaxSize()) {
                CustomWindowDraggableArea()
                FullSizeCircularProgressIndicator()
            }
        }

        AuthState.Unauthenticated -> AppNavigation(navigator, modifier)
        
        AuthState.Authenticated -> {
            navigator.clearAndGoTo(Route.Menus.Home)
            AppNavigation(navigator, modifier)
        }
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
private fun AppNavigation(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    authRepository: AuthRepository = koinInject()
) {
    if (navigator.backstack.last() is Route.Menus) {
        MainNavMenu(
            navigator = navigator,
            navigateToHome = { navigator.checkAndNavigate(Route.Menus.Home) },
            navigateToQuizzes = { navigator.checkAndNavigate(Route.Menus.Quizzes.QuizzesList) },
            navigateToUsers = { navigator.checkAndNavigate(Route.Menus.Users.UsersList) },
            navigateToUserProfile = { navigator.checkAndNavigate(Route.Menus.Users.UserDetails(authRepository.thisUser.value?.userId!!)) },
        ) {
            Navigation(navigator, modifier)
        }
    } else {
        Navigation(navigator, modifier)
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun Navigation(
    navigator: Navigator,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        modifier = modifier,
        backStack = navigator.backstack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = koinEntryProvider(),
    )
}