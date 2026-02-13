package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.AutoScalableLazyRow
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeErrorIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.home.presentation.HomeViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizCard
import com.github.projektmagma.magmaquiz.app.users.presentation.components.UserCard
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.send_invite
import org.koin.compose.viewmodel.koinViewModel
import java.util.*

@Composable
fun HomeScreen(
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
) {

    val viewModel = koinViewModel<HomeViewModel>()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val recentQuizzes = viewModel.recentQuizzes.collectAsStateWithLifecycle()
    val mostLikedQuizzes = viewModel.mostLikedQuizzes.collectAsStateWithLifecycle()
    val friendsQuizzes = viewModel.friendsQuizzes.collectAsStateWithLifecycle()
    val incomingFriends = viewModel.incomingFriends.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState.value) {
            is UiState.Error -> FullSizeErrorIndicator(
                onRetry = {
                    viewModel.downloadAllData()
                })

            UiState.Loading -> FullSizeCircularProgressIndicator()
            UiState.Success ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Dobrze cię widzieć!",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )


                        Text(
                            text = "Najbardziej lubiane quizy przez społeczność",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        AutoScalableLazyRow(
                            itemList = mostLikedQuizzes.value,
                            key = { it.id!! }
                        ) {
                            QuizCard(
                                it,
                                navigateToQuizDetails = navigateToQuizDetails,
                                navigateToUserDetails = navigateToUserDetails,
                                changeFavoriteStatus = {},
                            )
                        }

                        Text(
                            text = "Ostatnie quizy, które zostały dodane",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        AutoScalableLazyRow(
                            itemList = recentQuizzes.value,
                            key = { it.id!! }
                        ) {
                            QuizCard(
                                it,
                                navigateToQuizDetails = navigateToQuizDetails,
                                navigateToUserDetails = navigateToUserDetails,
                                changeFavoriteStatus = {},
                            )
                        }

                        Text(
                            text = "Osoby, które chcą cię poznać",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        AutoScalableLazyRow(
                            itemList = incomingFriends.value,
                            key = { it.userId!! }
                        ) {
                            UserCard(
                                it,
                                navigateToUserDetails = navigateToUserDetails,
                                inviteButtonText = Res.string.send_invite,
                                onInviteButtonClick = {},
                            )
                        }

                        Text(
                            text = "Nowe quizy twoich znajomych",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        AutoScalableLazyRow(
                            itemList = friendsQuizzes.value,
                            key = { it.id!! }
                        ) {
                            QuizCard(
                                it,
                                navigateToQuizDetails = navigateToQuizDetails,
                                navigateToUserDetails = navigateToUserDetails,
                                changeFavoriteStatus = {},
                            )
                        }
                    }
                }
        }
    }
}