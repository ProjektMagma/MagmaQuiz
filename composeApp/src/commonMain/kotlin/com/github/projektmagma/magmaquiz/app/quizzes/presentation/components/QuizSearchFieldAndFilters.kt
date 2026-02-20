package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FilterButton
import com.github.projektmagma.magmaquiz.app.core.presentation.components.SearchTextField
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizFilters
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizListCommand
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun QuizSearchFieldAndFilters(modifier: Modifier) {
    val quizzesListViewModel: QuizzesListViewModel = koinViewModel()
    val state by quizzesListViewModel.quizListState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        SearchTextField(
            modifier = Modifier
                .fillMaxWidth(),
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
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterButton(
                    selected = state.quizFilter == QuizFilters.Favorites,
                    onClick = {
                        quizzesListViewModel.onCommand(
                            QuizListCommand.FilterChanged(
                                if (state.quizFilter == QuizFilters.Favorites)
                                    QuizFilters.None
                                else QuizFilters.Favorites
                            )
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
                        quizzesListViewModel.onCommand(
                            QuizListCommand.FilterChanged(
                                if (state.quizFilter == QuizFilters.MostLiked)
                                    QuizFilters.None
                                else QuizFilters.MostLiked
                            )
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
                        quizzesListViewModel.onCommand(
                            QuizListCommand.FilterChanged(
                                if (state.quizFilter == QuizFilters.Friends)
                                    QuizFilters.None
                                else QuizFilters.Friends
                            )
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
                        quizzesListViewModel.onCommand(
                            QuizListCommand.FilterChanged(
                                if (state.quizFilter == QuizFilters.RecentlyAdded)
                                    QuizFilters.None
                                else QuizFilters.RecentlyAdded
                            )
                        )
                    },
                    contentLabel = stringResource(Res.string.recently_added),
                    contentIcon = Icons.Default.Upload
                )
            }
        }
    }
}