package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route
import com.github.projektmagma.magmaquiz.app.home.presentation.components.NavButton
import com.github.projektmagma.magmaquiz.app.home.presentation.components.ProfilePictureIcon
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun MainNavMenu(
    backStack: NavBackStack<NavKey>,
    navigateToHome: () -> Unit,
    navigateToQuizzes: () -> Unit,
    navigateToUsers: () -> Unit,
    navigateToSettings: () -> Unit,
    content: @Composable (() -> Unit)
) {

    val viewModel = koinViewModel<AuthViewModel>()
    val thisUser = viewModel.thisUser.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavButton(
                    isCurrentRoute = backStack.last() == Route.Main.Home,
                    onClick = {
                        navigateToHome()
                    },
                    contentLabel = "Home",
                    contentIcon = Icons.Default.Home
                )
                NavButton(
                    isCurrentRoute = backStack.last() == Route.Main.Quizzes,
                    onClick = {
                        navigateToQuizzes()
                    },
                    contentLabel = "Quizzes",
                    contentIcon = Icons.Default.Quiz
                )
                NavButton(
                    isCurrentRoute = backStack.last() == Route.Main.Users,
                    onClick = {
                        navigateToUsers()
                    },
                    contentLabel = "Users",
                    contentIcon = Icons.Default.PersonAdd
                )
            }
            Text(
                text = "Hello, ${thisUser.value!!.userName}!",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(20.dp))
            IconButton(
                onClick = { navigateToSettings() },
            ) {
                if (thisUser.value!!.userProfilePicture == null)
                    Icon(
                        modifier = Modifier.size(25.dp),
                        imageVector = Icons.Default.AccountCircle,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "SettingsButton",
                    )
                else
                    ProfilePictureIcon(thisUser.value!!.userProfilePicture!!)
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
