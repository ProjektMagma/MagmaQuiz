package com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.*
import com.github.projektmagma.magmaquiz.app.home.presentation.components.quizzes.QuizCard
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizFilters
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.*

@Composable
fun QuizzesScreen(
    navigateToCreateQuizScreen: () -> Unit,
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
    quizzesListViewModel: QuizzesListViewModel = koinViewModel()
) {
    val quizzes by quizzesListViewModel.quizzes.collectAsStateWithLifecycle()
    val uiState by quizzesListViewModel.uiState.collectAsStateWithLifecycle()
    var quizFilters by remember { quizzesListViewModel.quizFilters }


    LaunchedEffect(quizFilters) {
        when (quizFilters) {
            QuizFilters.Favorites -> quizzesListViewModel.getMyFavorites()
            QuizFilters.MostLiked -> quizzesListViewModel.getMostLikedQuizzes()
            QuizFilters.Friends -> quizzesListViewModel.getFriendsQuizzes()
            QuizFilters.RecentlyAdded -> quizzesListViewModel.getRecentlyAddedQuizzes()
            QuizFilters.None -> quizzesListViewModel.getQuizByName(false)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            searchedText = quizzesListViewModel.quizName,
            labelText = stringResource(Res.string.quiz_title),
            onSearch = {
                quizzesListViewModel.getQuizByName()
            },
            onValueChange = {
                quizzesListViewModel.quizName = it
                quizzesListViewModel.getQuizByName(true)
            },
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterButton(
                    selected = quizFilters == QuizFilters.Favorites,
                    onClick = {
                        quizFilters = if (quizFilters == QuizFilters.Favorites)
                            QuizFilters.None
                        else QuizFilters.Favorites
                    },
                    contentLabel = stringResource(Res.string.favorites),
                    contentIcon = Icons.Default.Favorite
                )
            }

            item {
                FilterButton(
                    selected = quizFilters == QuizFilters.MostLiked,
                    onClick = {
                        quizFilters = if (quizFilters == QuizFilters.MostLiked)
                            QuizFilters.None
                        else QuizFilters.MostLiked
                    },
                    contentLabel = stringResource(Res.string.most_liked),
                    contentIcon = Icons.Default.Recommend
                )
            }

            item {
                FilterButton(
                    selected = quizFilters == QuizFilters.Friends,
                    onClick = {
                        quizFilters = if (quizFilters == QuizFilters.Friends)
                            QuizFilters.None
                        else QuizFilters.Friends
                    },
                    contentLabel = stringResource(Res.string.friends_quizzes),
                    contentIcon = Icons.Default.Groups
                )
            }

            item {
                FilterButton(
                    selected = quizFilters == QuizFilters.RecentlyAdded,
                    onClick = {
                        quizFilters = if (quizFilters == QuizFilters.RecentlyAdded)
                            QuizFilters.None
                        else QuizFilters.RecentlyAdded
                    },
                    contentLabel = stringResource(Res.string.recently_added),
                    contentIcon = Icons.Default.Upload
                )
            }

            item {
                ButtonWithIcon(
                    contentLabel = stringResource(Res.string.create),
                    contentIcon = Icons.Default.Add,
                    onClick = {
                        navigateToCreateQuizScreen()
                    }
                )
            }
        }
        when (uiState) {
            is UiState.Error -> FullSizeErrorIndicator(
                message = (uiState as UiState.Error).errorMessage,
                onRetry = {
                    quizzesListViewModel.getQuizByName()
                }
            )

            UiState.Loading -> FullSizeCircularProgressIndicator()
            UiState.Success ->
                AutoScalableLazyColumn(quizzes, { it.id!! }) { quiz ->
                    QuizCard(
                        quiz = quiz,
                        navigateToQuizDetails = {
                            navigateToQuizDetails(quiz.id!!)
                        },
                        navigateToUserDetails = {
                            navigateToUserDetails(quiz.quizCreator?.userId!!)
                        },
                        changeFavoriteStatus = {
                            quizzesListViewModel.changeFavoriteStatus(quiz.id!!)
                        }
                    )
                }
        }
    }
}
