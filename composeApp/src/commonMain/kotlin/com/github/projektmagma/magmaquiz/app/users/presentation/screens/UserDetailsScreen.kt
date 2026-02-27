package com.github.projektmagma.magmaquiz.app.users.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FriendshipButtons
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeErrorIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizCardSmall
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.QuizCommand
import com.github.projektmagma.magmaquiz.app.users.presentation.UserDetailsViewModel
import com.github.projektmagma.magmaquiz.app.users.presentation.UsersSharedViewModel
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.game_history
import magmaquiz.composeapp.generated.resources.quizzes
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun UserDetailsScreen(
    id: UUID,
    navigateToEditScreen: (id: UUID) -> Unit,
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToSettingsScreen: () -> Unit,
    userDetailsViewModel: UserDetailsViewModel,
    createQuizViewModel: CreateQuizViewModel = koinViewModel(),
    usersSharedViewModel: UsersSharedViewModel = koinViewModel()
) {
    val quizzes by userDetailsViewModel.quizzes.collectAsStateWithLifecycle()
    val user by usersSharedViewModel.user.collectAsStateWithLifecycle()
    val uiState by userDetailsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (user?.userId != id) {
            userDetailsViewModel.loadData(id)
        }
    }

    when (uiState) {
        is UiState.Error -> FullSizeErrorIndicator(
            message = (uiState as UiState.Error).errorMessage,
            onRetry = {
                userDetailsViewModel.loadData(id)
            }
        )

        UiState.Loading -> FullSizeCircularProgressIndicator()
        UiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 1000.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ProfilePictureIcon(
                                    imageData = user?.userProfilePicture,
                                    size = 64.dp
                                )
                                Text(
                                    text = user?.userName ?: "",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            if (userDetailsViewModel.checkOwnership(id)) {
                                IconButton(
                                    onClick = { navigateToSettingsScreen() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Settings"
                                    )
                                }
                            }
                        }

                        val foreignUser = user as? ForeignUser

                        if (foreignUser != null) {
                            FriendshipButtons(
                                user = foreignUser,
                                usersSharedViewModel
                            )
                        }
                        
                        var selectedTabIndex by remember { mutableIntStateOf(0) }

                        if (foreignUser == null){
                            SecondaryTabRow(
                                selectedTabIndex = selectedTabIndex,
                                divider = { HorizontalDivider() },
                            ) {
                                Tab(
                                    selected = selectedTabIndex == 0,
                                    onClick = {
                                        selectedTabIndex = 0
                                        userDetailsViewModel.getQuizzesByUserId(id)
                                    },
                                ) {
                                    Text(stringResource(Res.string.quizzes))
                                }
                                Tab(
                                    selected = selectedTabIndex == 1,
                                    onClick = {
                                        selectedTabIndex = 1
                                        userDetailsViewModel.getUserHistory()
                                    },
                                ) {
                                    Text(stringResource(Res.string.game_history))
                                }
                            }
                        }
                    }
                }

                if (quizzes != null) {
                    items(quizzes!!) { quiz ->
                        QuizCardSmall(
                            quiz = quiz,
                            showUserButton = false,
                            navigateToQuizDetails = { navigateToQuizDetails(quiz.id!!) },
                            changeFavoriteStatus = { userDetailsViewModel.changeFavoriteStatus(quiz.id!!) },
                            navigateToEditScreen = { navigateToEditScreen(quiz.id!!) },
                            canEdit = userDetailsViewModel.checkOwnership(quiz.quizCreator?.userId!!),
                            onDeleteClick = { userDetailsViewModel.deleteQuiz(quiz.id!!) },
                            onEditClick = { createQuizViewModel.onCommand(QuizCommand.SetForEdit(quiz.id!!)) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    item {
                        FullSizeCircularProgressIndicator()
                    }
                }
            }
        }
    }
}