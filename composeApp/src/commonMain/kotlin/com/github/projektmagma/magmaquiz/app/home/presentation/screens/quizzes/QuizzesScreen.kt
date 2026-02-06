package com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.AutoScalableLazyColumn
import com.github.projektmagma.magmaquiz.app.home.presentation.components.FilterButton
import com.github.projektmagma.magmaquiz.app.home.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.home.presentation.components.FullSizeErrorIndicator
import com.github.projektmagma.magmaquiz.app.home.presentation.components.SearchTextField
import com.github.projektmagma.magmaquiz.app.home.presentation.components.quizzes.QuizCard
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizFilters
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizListCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.favorites
import magmaquiz.composeapp.generated.resources.friends_quizzes
import magmaquiz.composeapp.generated.resources.most_liked
import magmaquiz.composeapp.generated.resources.quiz_title
import magmaquiz.composeapp.generated.resources.recently_added
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun QuizzesScreen(
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
    quizzesListViewModel: QuizzesListViewModel = koinViewModel()
) {
    val state by quizzesListViewModel.quizListState.collectAsStateWithLifecycle()
    val uiState by quizzesListViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.quizFilter) {
        if (!state.isLoaded) quizzesListViewModel.onCommand(QuizListCommand.LoadByFilter)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            searchedText = state.quizName,
            labelText = stringResource(Res.string.quiz_title),
            onSearch = {
                quizzesListViewModel.onCommand(QuizListCommand.LoadByName())
            },
            onValueChange = {
                quizzesListViewModel.onCommand(QuizListCommand.NameChanged(it))
                quizzesListViewModel.onCommand(QuizListCommand.LoadByName(true))
            },
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterButton(
                    selected = state.quizFilter == QuizFilters.Favorites,
                    onClick = {
                        quizzesListViewModel.onCommand(QuizListCommand.FilterChanged(
                            if (state.quizFilter == QuizFilters.Favorites)
                                QuizFilters.None
                            else QuizFilters.Favorites)
                        )
                    },
                    contentLabel = stringResource(Res.string.favorites),
                    contentIcon = Icons.Default.Favorite
                )
            }

            item {
                FilterButton(
                    selected = state.quizFilter == QuizFilters.MostLiked,
                    onClick = {
                        quizzesListViewModel.onCommand(QuizListCommand.FilterChanged(
                            if (state.quizFilter == QuizFilters.MostLiked)
                                QuizFilters.None
                            else QuizFilters.MostLiked)
                        )
                    },
                    contentLabel = stringResource(Res.string.most_liked),
                    contentIcon = Icons.Default.Recommend
                )
            }

            item {
                FilterButton(
                    selected = state.quizFilter == QuizFilters.Friends,
                    onClick = {
                        quizzesListViewModel.onCommand(QuizListCommand.FilterChanged(
                            if (state.quizFilter == QuizFilters.Friends)
                                QuizFilters.None
                            else QuizFilters.Friends)
                        )
                    },
                    contentLabel = stringResource(Res.string.friends_quizzes),
                    contentIcon = Icons.Default.Groups
                )
            }

            item {
                FilterButton(
                    selected = state.quizFilter == QuizFilters.RecentlyAdded,
                    onClick = {
                        quizzesListViewModel.onCommand(QuizListCommand.FilterChanged(
                            if (state.quizFilter == QuizFilters.RecentlyAdded)
                                QuizFilters.None
                            else QuizFilters.RecentlyAdded)
                        )
                    },
                    contentLabel = stringResource(Res.string.recently_added),
                    contentIcon = Icons.Default.Upload
                )
            }
        }
        when (uiState) {
            is UiState.Error -> FullSizeErrorIndicator(
                message = (uiState as UiState.Error).errorMessage,
                onRetry = {
                    quizzesListViewModel.onCommand(QuizListCommand.LoadByName())
                }
            )

            UiState.Loading -> FullSizeCircularProgressIndicator()
            UiState.Success ->
                AutoScalableLazyColumn(state.quizzes, { it.id!! }) { quiz ->
                    QuizCard(
                        quiz = quiz,
                        navigateToQuizDetails = {
                            navigateToQuizDetails(quiz.id!!)
                        },
                        navigateToUserDetails = {
                            navigateToUserDetails(quiz.quizCreator?.userId!!)
                        },
                        changeFavoriteStatus = {
                            quizzesListViewModel.onCommand(QuizListCommand.FavoriteStatusChanged(quiz.id!!))
                        }
                    )
                }
        }
    }
}
