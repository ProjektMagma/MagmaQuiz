package com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.*
import com.github.projektmagma.magmaquiz.app.home.presentation.components.quizzes.QuizCard
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.create
import magmaquiz.composeapp.generated.resources.favorites
import magmaquiz.composeapp.generated.resources.quiz_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.*

@Composable
fun QuizzesScreen(
    navigateToCreateQuizScreen: () -> Unit,
    navigateToQuizDetails: (id: UUID) -> Unit,
    quizzesListViewModel: QuizzesListViewModel = koinViewModel()
) {
    val quizzes by quizzesListViewModel.quizzes.collectAsStateWithLifecycle()
    val uiState by quizzesListViewModel.uiState.collectAsStateWithLifecycle()
    var isOnFavorites by remember { quizzesListViewModel.isOnFavorites }

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
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterButton(
                    selected = isOnFavorites,
                    onClick = {
                        isOnFavorites = !isOnFavorites
                        if (isOnFavorites) quizzesListViewModel.getMyFavorites()
                        else quizzesListViewModel.getQuizByName(false)
                    },
                    contentLabel = stringResource(Res.string.favorites),
                    contentIcon = Icons.Default.Favorite
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
                            // TODO: Nawigacja (na początek zakładka users a potem szczegóły jak będą)
                        },
                        changeFavoriteStatus = {
                            quizzesListViewModel.changeFavoriteStatus(quiz.id!!)
                        }
                    )
                }
        }
    }
}
