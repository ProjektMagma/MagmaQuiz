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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.snapshotFlow
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
import com.github.projektmagma.magmaquiz.app.users.presentation.model.details.UserDetailsCommand
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import kotlinx.coroutines.flow.distinctUntilChanged
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.game_history
import magmaquiz.composeapp.generated.resources.quizzes
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Composable
fun UserDetailsScreen(
    id: UUID,
    navigateToEditScreen: (id: UUID) -> Unit,
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToQuizReviews: (id: UUID, reviewed: Boolean) -> Unit,
    navigateToSettingsScreen: () -> Unit,
    createQuizViewModel: CreateQuizViewModel = koinViewModel(),
    usersSharedViewModel: UsersSharedViewModel = koinViewModel()
) {
    val userDetailsViewModel: UserDetailsViewModel = koinViewModel { parametersOf(id) }
    
    val uiState by userDetailsViewModel.uiState.collectAsStateWithLifecycle()
    
    val state by userDetailsViewModel.state.collectAsStateWithLifecycle()
    
    val lazyListState = rememberLazyListState()
    LaunchedEffect(state.quizzes){
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == state.quizzes?.lastIndex) {
                    userDetailsViewModel.onCommand(UserDetailsCommand.LoadNextItems)
                }
            }
    }

    when (uiState) {
        is UiState.Error -> FullSizeErrorIndicator(
            message = (uiState as UiState.Error).errorMessage,
            onRetry = {
                userDetailsViewModel.onCommand(UserDetailsCommand.LoadData)
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
                                    imageData = state.user?.userProfilePicture,
                                    size = 64.dp
                                )
                                Column {
                                    Text(
                                        text = state.user?.userName ?: "",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (state.user?.userTown?.isNotEmpty() == true || state.user?.userCountryCode?.isNotEmpty() == true){
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null
                                            )
                                            Text(
                                                modifier = Modifier.padding(end = 4.dp),
                                                text = state.user?.userCountryCode ?: "",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = state.user?.userTown ?: "",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
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

                        Text(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            text = state.user?.userBio ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        val foreignUser = state.user as? ForeignUser

                        if (foreignUser != null) {
                            FriendshipButtons(
                                user = foreignUser,
                                usersSharedViewModel
                            )
                        }

                        if (foreignUser == null){
                            SecondaryTabRow(
                                selectedTabIndex = state.selectedTabIndex,
                                divider = { HorizontalDivider() },
                            ) {
                                Tab(
                                    selected = state.selectedTabIndex == 0,
                                    onClick = {
                                        userDetailsViewModel.onCommand(UserDetailsCommand.SelectedTabIndexChanged(0))
                                        userDetailsViewModel.onCommand(UserDetailsCommand.LoadQuizzesOrHistory)
                                    },
                                    enabled = !state.isLoadingMore
                                ) {
                                    Text(stringResource(Res.string.quizzes))
                                }
                                Tab(
                                    selected = state.selectedTabIndex == 1,
                                    onClick = {
                                        userDetailsViewModel.onCommand(UserDetailsCommand.SelectedTabIndexChanged(1))
                                        userDetailsViewModel.onCommand(UserDetailsCommand.LoadQuizzesOrHistory)
                                    },
                                    enabled = !state.isLoadingMore
                                ) {
                                    Text(stringResource(Res.string.game_history))
                                }
                            }
                        }
                    }
                }

                if (state.quizzes != null) {
                    items(state.quizzes!!) { quiz ->
                        QuizCardSmall(
                            quiz = quiz,
                            showUserButton = false,
                            navigateToQuizDetails = { navigateToQuizDetails(quiz.id!!) },
                            changeFavoriteStatus = { userDetailsViewModel.onCommand(UserDetailsCommand.ChangeFavoriteStatus(quiz.id!!)) },
                            navigateToEditScreen = { navigateToEditScreen(quiz.id!!) },
                            canEdit = userDetailsViewModel.checkOwnership(quiz.quizCreator?.userId!!),
                            onDeleteClick = { userDetailsViewModel.onCommand(UserDetailsCommand.DeleteQuiz(quiz.id!!)) },
                            onEditClick = { createQuizViewModel.onCommand(QuizCommand.SetForEdit(quiz.id!!)) },
                            navigateToQuizReviews = { navigateToQuizReviews(quiz.id!!, quiz.reviewedByYou) },
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (state.isLoadingMore){
                    item {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}