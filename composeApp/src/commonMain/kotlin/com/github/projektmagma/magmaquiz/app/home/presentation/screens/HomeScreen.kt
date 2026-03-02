package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizCardSmall
import com.github.projektmagma.magmaquiz.app.users.presentation.components.UserCard
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun HomeScreen(
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
) {

    val viewModel = koinViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recentQuizzes by viewModel.recentQuizzes.collectAsStateWithLifecycle()
    val mostLikedQuizzes by viewModel.mostLikedQuizzes.collectAsStateWithLifecycle()
    val friendsQuizzes by viewModel.friendsQuizzes.collectAsStateWithLifecycle()
    val incomingFriends by viewModel.incomingFriends.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState) {
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
                            itemList = mostLikedQuizzes,
                            key = { it.id!! }
                        ) {
                            QuizCardSmall(
                                it,
                                navigateToQuizDetails = navigateToQuizDetails,
                                navigateToUserDetails = navigateToUserDetails,
                                changeFavoriteStatus = { viewModel.changeFavoriteStatus(it.id!!) }
                            )
                        }

                        Text(
                            text = "Ostatnie quizy, które zostały dodane",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        AutoScalableLazyRow(
                            itemList = recentQuizzes,
                            key = { it.id!! }
                        ) {
                            QuizCardSmall(
                                it,
                                navigateToQuizDetails = navigateToQuizDetails,
                                navigateToUserDetails = navigateToUserDetails,
                                changeFavoriteStatus = { viewModel.changeFavoriteStatus(it.id!!) }
                            )
                        }

                        Text(
                            text = "Osoby, które chcą cię poznać",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        AutoScalableLazyRow(
                            itemList = incomingFriends,
                            key = { it.userId!! }
                        ) {
                            UserCard(
                                it,
                                navigateToUserDetails = navigateToUserDetails,
                                usersSharedViewModel = koinViewModel()
                            )
                        }

                        Text(
                            text = "Nowe quizy twoich znajomych",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        AutoScalableLazyRow(
                            itemList = friendsQuizzes,
                            key = { it.id!! }
                        ) {
                            QuizCardSmall(
                                it,
                                navigateToQuizDetails = navigateToQuizDetails,
                                navigateToUserDetails = navigateToUserDetails,
                                changeFavoriteStatus = { viewModel.changeFavoriteStatus(it.id!!) }
                            )
                        }
                    }
                }
        }
    }
}