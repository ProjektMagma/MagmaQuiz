package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.AutoScalableLazyRow
import com.github.projektmagma.magmaquiz.app.core.presentation.components.WideTonalButton
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.home.presentation.HomeViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.RowLoadingIndicator
import com.github.projektmagma.magmaquiz.app.home.presentation.components.RowRetryButton
import com.github.projektmagma.magmaquiz.app.home.presentation.model.main.HomeScreenCommand
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizCardSmall
import com.github.projektmagma.magmaquiz.app.users.presentation.components.UserCardSmall
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.*

@Composable
fun HomeScreen(
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
    navigateToQuizReviews: (id: UUID, reviewed: Boolean) -> Unit,
    navigateToRoomList: () -> Unit
) {

    val viewModel = koinViewModel<HomeViewModel>()

    val recentQuizzesUiState by viewModel.recentQuizzesUiState.collectAsStateWithLifecycle()
    val mostLikedQuizzesUiState by viewModel.mostLikedQuizzesUiState.collectAsStateWithLifecycle()
    val incomingFriendsUiState by viewModel.incomingFriendsUiState.collectAsStateWithLifecycle()
    val friendsQuizzesUiState by viewModel.friendsQuizzesUiState.collectAsStateWithLifecycle()

    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 32.dp)) {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.good_to_see_you),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                WideTonalButton(
                    text = Res.string.find_games,
                    action = { navigateToRoomList() }
                )

                Text(
                    text = stringResource(Res.string.the_most_liked_quizzes),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                when (mostLikedQuizzesUiState) {
                    is UiState.Error -> {
                        RowRetryButton(
                            message = stringResource((mostLikedQuizzesUiState as UiState.Error).errorMessage),
                            onClick = { viewModel.onCommand(HomeScreenCommand.MostLikedQuizzes) }
                        )
                    }

                    UiState.Loading -> {
                        RowLoadingIndicator()
                    }

                    UiState.Success -> {
                        AutoScalableLazyRow(
                            itemList = state.mostLikedQuizzes,
                            modifier = Modifier.height(300.dp),
                            key = { it.id!! },
                            isLoadingMore = state.isLoadingMoreLiked,
                            onLoadMore = { viewModel.onCommand(HomeScreenCommand.MostLikedQuizzes) }
                        ) {
                            QuizCardSmall(
                                it,
                                navigateToQuizDetails = navigateToQuizDetails,
                                navigateToUserDetails = navigateToUserDetails,
                                changeFavoriteStatus = { viewModel.onCommand(HomeScreenCommand.ChangeFavorite(it.id!!)) },
                                navigateToQuizReviews = { navigateToQuizReviews(it.id!!, it.reviewedByYou) }
                            )
                        }
                    }
                }


                Text(
                    text = stringResource(Res.string.recently_added_quizzes),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )


                when (recentQuizzesUiState) {
                    is UiState.Error -> {
                        RowRetryButton(
                            message = stringResource((recentQuizzesUiState as UiState.Error).errorMessage),
                            onClick = { viewModel.onCommand(HomeScreenCommand.RecentQuizzes) }
                        )
                    }

                    UiState.Loading -> {
                        RowLoadingIndicator()
                    }

                    UiState.Success -> {

                        AutoScalableLazyRow(
                            itemList = state.recentQuizzes,
                            modifier = Modifier.height(300.dp),
                            key = { it.id!! },
                            isLoadingMore = state.isLoadingMoreRecent,
                            onLoadMore = { viewModel.onCommand(HomeScreenCommand.RecentQuizzes) }
                        ) {
                            QuizCardSmall(
                                it,
                                navigateToQuizDetails = navigateToQuizDetails,
                                navigateToUserDetails = navigateToUserDetails,
                                changeFavoriteStatus = { viewModel.onCommand(HomeScreenCommand.ChangeFavorite(it.id!!)) },
                                navigateToQuizReviews = { navigateToQuizReviews(it.id!!, it.reviewedByYou) }
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(Res.string.pepole_who_whant_know_you),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                when (incomingFriendsUiState) {
                    is UiState.Error -> {
                        RowRetryButton(
                            message = stringResource((incomingFriendsUiState as UiState.Error).errorMessage),
                            onClick = { viewModel.onCommand(HomeScreenCommand.IncomingFriends) }
                        )
                    }

                    UiState.Loading -> {
                        RowLoadingIndicator()
                    }

                    UiState.Success -> {
                        AutoScalableLazyRow(
                            itemList = state.incomingFriends,
                            modifier = Modifier.height(260.dp),
                            key = { it.userId!! },
                            isLoadingMore = state.isLoadingMoreFriends,
                            onLoadMore = { viewModel.onCommand(HomeScreenCommand.IncomingFriends) }
                        ) {
                            UserCardSmall(
                                it,
                                navigateToUserDetails = navigateToUserDetails,
                                usersSharedViewModel = koinViewModel()
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(Res.string.new_quizzes_from_your_friends),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                when (friendsQuizzesUiState) {
                    is UiState.Error -> {
                        RowRetryButton(
                            message = stringResource((friendsQuizzesUiState as UiState.Error).errorMessage),
                            onClick = { viewModel.onCommand(HomeScreenCommand.FriendsQuizzes) }
                        )
                    }

                    UiState.Loading -> {
                        RowLoadingIndicator()
                    }

                    UiState.Success -> {
                        AutoScalableLazyRow(
                            itemList = state.friendsQuizzes,
                            modifier = Modifier.height(300.dp),
                            key = { it.id!! },
                            isLoadingMore = state.isLoadingMoreFriendsQuizzes,
                            onLoadMore = { viewModel.onCommand(HomeScreenCommand.FriendsQuizzes) }
                        ) {
                            QuizCardSmall(
                                it,
                                navigateToQuizDetails = navigateToQuizDetails,
                                navigateToUserDetails = navigateToUserDetails,
                                changeFavoriteStatus = { viewModel.onCommand(HomeScreenCommand.ChangeFavorite(it.id!!)) },
                                navigateToQuizReviews = { navigateToQuizReviews(it.id!!, it.reviewedByYou) }
                            )
                        }
                    }
                }
            }
        }
    }
}
